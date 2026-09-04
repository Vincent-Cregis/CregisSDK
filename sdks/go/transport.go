package cregis

import (
	"bytes"
	"context"
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net"
	"net/http"
	"net/url"
	"reflect"
	"strings"
	"time"
)

const maxResponseBytes int64 = 16 << 20

type operation struct {
	id     string
	method string
	path   string
}

type authenticator interface {
	authenticate(path string, payload []byte) ([]byte, http.Header, error)
}

type baseClient struct {
	baseURL       string
	httpClient    *http.Client
	authenticator authenticator
}

type projectAuthenticator struct {
	projectID int64
	apiKey    string
}

type teamAuthenticator struct {
	accessKey    string
	accessSecret string
}

type requestValidator interface {
	Validate() error
}

func newProjectBase(config ProjectConfig) (*baseClient, error) {
	if config.ProjectID <= 0 || config.ProjectID > maxSafeProjectID {
		return nil, &ClientError{Message: "ProjectID must be a positive JavaScript-safe int64 value"}
	}
	if strings.TrimSpace(config.APIKey) == "" {
		return nil, &ClientError{Message: "API Key is required"}
	}
	baseURL, err := normalizeBaseURL(config.BaseURL)
	if err != nil {
		return nil, err
	}
	httpClient, err := configuredHTTPClient(config.HTTPClient, config.Timeout)
	if err != nil {
		return nil, err
	}
	return &baseClient{
		baseURL:       baseURL,
		httpClient:    httpClient,
		authenticator: &projectAuthenticator{projectID: config.ProjectID, apiKey: config.APIKey},
	}, nil
}

func newTeamBase(config TeamConfig) (*baseClient, error) {
	if strings.TrimSpace(config.AccessKey) == "" {
		return nil, &ClientError{Message: "Access Key is required"}
	}
	if strings.TrimSpace(config.AccessSecret) == "" {
		return nil, &ClientError{Message: "Access Secret is required"}
	}
	baseURL, err := normalizeBaseURL(config.BaseURL)
	if err != nil {
		return nil, err
	}
	httpClient, err := configuredHTTPClient(config.HTTPClient, config.Timeout)
	if err != nil {
		return nil, err
	}
	return &baseClient{
		baseURL:       baseURL,
		httpClient:    httpClient,
		authenticator: &teamAuthenticator{accessKey: config.AccessKey, accessSecret: config.AccessSecret},
	}, nil
}

func normalizeBaseURL(raw string) (string, error) {
	trimmed := strings.TrimSpace(raw)
	parsed, err := url.Parse(trimmed)
	if err != nil || parsed.Scheme == "" || parsed.Host == "" || parsed.Hostname() == "" {
		return "", &ClientError{Message: "Base URL must be a valid HTTP(S) URL", Cause: err}
	}
	if parsed.User != nil {
		return "", &ClientError{Message: "Base URL must not contain user information"}
	}
	if parsed.RawQuery != "" || parsed.Fragment != "" {
		return "", &ClientError{Message: "Base URL must not contain a query or fragment"}
	}
	loopback := parsed.Hostname() == "localhost"
	if ip := net.ParseIP(parsed.Hostname()); ip != nil && ip.IsLoopback() {
		loopback = true
	}
	if parsed.Scheme != "https" && !(parsed.Scheme == "http" && loopback) {
		return "", &ClientError{Message: "Base URL must use HTTPS"}
	}
	parsed.Path = strings.TrimRight(parsed.Path, "/")
	parsed.RawPath = ""
	return strings.TrimRight(parsed.String(), "/"), nil
}

func configuredHTTPClient(provided *http.Client, timeout time.Duration) (*http.Client, error) {
	if timeout < 0 {
		return nil, &ClientError{Message: "Timeout must not be negative"}
	}
	if timeout == 0 {
		timeout = defaultTimeout
	}
	resolved := http.DefaultClient
	if provided != nil {
		resolved = provided
	}
	copy := *resolved
	copy.Timeout = timeout
	copy.CheckRedirect = func(_ *http.Request, _ []*http.Request) error {
		return http.ErrUseLastResponse
	}
	return &copy, nil
}

func (auth *projectAuthenticator) authenticate(_ string, payload []byte) ([]byte, http.Header, error) {
	parameters, err := decodeJSONObject(payload)
	if err != nil {
		return nil, nil, &ContractError{Context: "project request", Cause: err}
	}
	nonce, err := randomHex(3)
	if err != nil {
		return nil, nil, &ClientError{Message: "Failed to generate request nonce", Cause: err}
	}
	parameters["pid"] = auth.projectID
	parameters["nonce"] = nonce
	parameters["timestamp"] = time.Now().UnixMilli()
	signature, err := SignProjectParameters(parameters, auth.apiKey)
	if err != nil {
		return nil, nil, err
	}
	parameters["sign"] = signature
	body, err := json.Marshal(parameters)
	if err != nil {
		return nil, nil, &ClientError{Message: "Failed to serialize signed request", Cause: err}
	}
	return body, make(http.Header), nil
}

func (auth *teamAuthenticator) authenticate(path string, payload []byte) ([]byte, http.Header, error) {
	body, err := CanonicalizeJSON(payload)
	if err != nil {
		return nil, nil, err
	}
	timestamp := time.Now().UnixMilli()
	nonce, err := randomHex(16)
	if err != nil {
		return nil, nil, &ClientError{Message: "Failed to generate request nonce", Cause: err}
	}
	signature, err := SignTeamRequest(path, timestamp, nonce, body, auth.accessSecret)
	if err != nil {
		return nil, nil, err
	}
	headers := make(http.Header)
	headers.Set("Access-Key", auth.accessKey)
	headers.Set("Access-Timestamp", fmt.Sprintf("%d", timestamp))
	headers.Set("Access-Nonce", nonce)
	headers.Set("Access-Signature", signature)
	return body, headers, nil
}

func randomHex(size int) (string, error) {
	value := make([]byte, size)
	if _, err := io.ReadFull(rand.Reader, value); err != nil {
		return "", err
	}
	return hex.EncodeToString(value), nil
}

func execute[Response any](
	ctx context.Context,
	client *baseClient,
	operation operation,
	request any,
) (*Response, error) {
	var response Response
	if err := client.post(ctx, operation, request, &response); err != nil {
		return nil, err
	}
	return &response, nil
}

func executeNoResponse(
	ctx context.Context,
	client *baseClient,
	operation operation,
	request any,
) error {
	return client.post(ctx, operation, request, nil)
}

func (client *baseClient) post(
	ctx context.Context,
	operation operation,
	requestValue any,
	responseValue any,
) error {
	if client == nil {
		return &ClientError{Message: "Cregis client is nil"}
	}
	if ctx == nil {
		return &ClientError{Message: "Context is required"}
	}
	if operation.method != http.MethodPost {
		return &ClientError{Message: "Unsupported HTTP method: " + operation.method}
	}
	payload, err := marshalRequest(operation, requestValue)
	if err != nil {
		return err
	}
	body, authHeaders, err := client.authenticator.authenticate(operation.path, payload)
	if err != nil {
		return err
	}
	httpRequest, err := http.NewRequestWithContext(
		ctx,
		operation.method,
		client.baseURL+operation.path,
		bytes.NewReader(body),
	)
	if err != nil {
		return &ClientError{Message: "Failed to create Cregis request", Cause: err}
	}
	httpRequest.Header.Set("Accept", "application/json")
	httpRequest.Header.Set("Content-Type", "application/json; charset=utf-8")
	httpRequest.Header.Set("User-Agent", "cregis-go/"+Version)
	for name, values := range authHeaders {
		for _, value := range values {
			httpRequest.Header.Add(name, value)
		}
	}

	httpResponse, err := client.httpClient.Do(httpRequest)
	if err != nil {
		return &ClientError{
			Message: fmt.Sprintf("Network error executing POST %s", operation.path),
			Cause:   err,
		}
	}
	defer httpResponse.Body.Close()
	responseBody, err := io.ReadAll(io.LimitReader(httpResponse.Body, maxResponseBytes+1))
	if err != nil {
		return &ClientError{Message: "Failed to read Cregis response", Cause: err}
	}
	if int64(len(responseBody)) > maxResponseBytes {
		return &ClientError{Message: "Cregis response exceeded the 16 MiB limit"}
	}
	if httpResponse.StatusCode < 200 || httpResponse.StatusCode >= 300 {
		return &HTTPError{
			StatusCode:   httpResponse.StatusCode,
			Status:       httpResponse.Status,
			ResponseBody: string(responseBody),
		}
	}

	envelope, err := decodeEnvelope(responseBody)
	if err != nil {
		return &ContractError{Context: operation.id + " response envelope", Cause: err}
	}
	if envelope.Code != "00000" {
		return &APIError{Code: envelope.Code, Message: envelope.Message}
	}
	if responseValue == nil {
		return nil
	}
	if bytes.Equal(bytes.TrimSpace(envelope.Data), []byte("null")) {
		return &ContractError{
			Context: operation.id + " response",
			Cause:   errors.New("response data must not be null"),
		}
	}
	if err := decodeJSON(envelope.Data, responseValue); err != nil {
		return &ContractError{Context: operation.id + " response", Cause: err}
	}
	return nil
}

func marshalRequest(operation operation, value any) ([]byte, error) {
	if value == nil {
		return []byte("{}"), nil
	}
	reflected := reflect.ValueOf(value)
	if reflected.Kind() == reflect.Ptr && reflected.IsNil() {
		return nil, &ContractError{
			Context: operation.id + " request",
			Cause:   errors.New("request must not be nil"),
		}
	}
	if validator, ok := value.(requestValidator); ok {
		if err := validator.Validate(); err != nil {
			return nil, &ContractError{Context: operation.id + " request", Cause: err}
		}
	}
	payload, err := json.Marshal(value)
	if err != nil {
		return nil, &ContractError{Context: operation.id + " request", Cause: err}
	}
	return payload, nil
}

type responseEnvelope struct {
	Code    string
	Message string
	Data    json.RawMessage
}

func decodeEnvelope(data []byte) (*responseEnvelope, error) {
	var wire struct {
		Code *string         `json:"code"`
		Msg  *string         `json:"msg"`
		Data json.RawMessage `json:"data"`
	}
	if err := decodeJSON(data, &wire); err != nil {
		return nil, err
	}
	if wire.Code == nil || strings.TrimSpace(*wire.Code) == "" {
		return nil, errors.New("response is missing required string field: code")
	}
	if wire.Msg == nil {
		return nil, errors.New("response is missing required string field: msg")
	}
	if wire.Data == nil {
		return nil, errors.New("response is missing required field: data")
	}
	return &responseEnvelope{Code: *wire.Code, Message: *wire.Msg, Data: wire.Data}, nil
}

func decodeJSONObject(data []byte) (map[string]any, error) {
	var result map[string]any
	decoder := json.NewDecoder(bytes.NewReader(data))
	decoder.UseNumber()
	if err := decoder.Decode(&result); err != nil {
		return nil, err
	}
	if result == nil {
		return nil, errors.New("JSON value must be an object")
	}
	if err := ensureJSONEOF(decoder); err != nil {
		return nil, err
	}
	return result, nil
}

func decodeJSON(data []byte, destination any) error {
	decoder := json.NewDecoder(bytes.NewReader(data))
	if err := decoder.Decode(destination); err != nil {
		return err
	}
	return ensureJSONEOF(decoder)
}

func ensureJSONEOF(decoder *json.Decoder) error {
	var trailing any
	if err := decoder.Decode(&trailing); !errors.Is(err, io.EOF) {
		if err == nil {
			return errors.New("JSON value has trailing content")
		}
		return err
	}
	return nil
}

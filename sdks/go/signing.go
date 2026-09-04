package cregis

import (
	"bytes"
	"crypto/hmac"
	"crypto/md5" // #nosec G501 -- MD5 is required by the Cregis wire protocol.
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"math"
	"math/big"
	"sort"
	"strconv"
	"strings"

	"github.com/gowebpki/jcs"
)

// CanonicalizeJSON returns the RFC 8785 representation of a value or raw JSON.
func CanonicalizeJSON(value any) ([]byte, error) {
	var raw []byte
	switch typed := value.(type) {
	case []byte:
		raw = append([]byte(nil), typed...)
	case string:
		raw = []byte(typed)
	default:
		var err error
		raw, err = json.Marshal(value)
		if err != nil {
			return nil, &ClientError{Message: "JSON value cannot be canonicalized", Cause: err}
		}
	}
	if len(strings.TrimSpace(string(raw))) == 0 {
		return nil, &ClientError{Message: "JSON value cannot be canonicalized"}
	}
	if err := validateCanonicalJSONNumbers(raw); err != nil {
		return nil, &ClientError{Message: "JSON value cannot be canonicalized", Cause: err}
	}
	canonical, err := jcs.Transform(raw)
	if err != nil {
		return nil, &ClientError{Message: "JSON value cannot be canonicalized", Cause: err}
	}
	return canonical, nil
}

// SignProjectParameters returns the Payment Engine/WaaS MD5 parameter signature.
func SignProjectParameters(parameters map[string]any, apiKey string) (string, error) {
	if parameters == nil {
		return "", &ClientError{Message: "Signature parameters must be a JSON object"}
	}
	if strings.TrimSpace(apiKey) == "" {
		return "", &ClientError{Message: "API Key is required"}
	}
	keys := make([]string, 0, len(parameters))
	for key := range parameters {
		keys = append(keys, key)
	}
	sort.Strings(keys)

	var signingText strings.Builder
	signingText.WriteString(apiKey)
	for _, key := range keys {
		value := parameters[key]
		if key == "sign" || value == nil {
			continue
		}
		encoded, err := stringifySignatureValue(value)
		if err != nil {
			return "", err
		}
		if encoded == "" {
			continue
		}
		signingText.WriteString(key)
		signingText.WriteString(encoded)
	}
	digest := md5.Sum([]byte(signingText.String())) // #nosec G401 -- protocol compatibility.
	return hex.EncodeToString(digest[:]), nil
}

func stringifySignatureValue(value any) (string, error) {
	switch typed := value.(type) {
	case string:
		return typed, nil
	case bool:
		return strconv.FormatBool(typed), nil
	case int:
		return strconv.Itoa(typed), nil
	case int8:
		return strconv.FormatInt(int64(typed), 10), nil
	case int16:
		return strconv.FormatInt(int64(typed), 10), nil
	case int32:
		return strconv.FormatInt(int64(typed), 10), nil
	case int64:
		return strconv.FormatInt(typed, 10), nil
	case uint:
		return strconv.FormatUint(uint64(typed), 10), nil
	case uint8:
		return strconv.FormatUint(uint64(typed), 10), nil
	case uint16:
		return strconv.FormatUint(uint64(typed), 10), nil
	case uint32:
		return strconv.FormatUint(uint64(typed), 10), nil
	case uint64:
		return strconv.FormatUint(typed, 10), nil
	case json.Number:
		return stringifyJSONNumber(typed)
	}
	canonical, err := CanonicalizeJSON(value)
	if err != nil {
		return "", &ClientError{Message: "Failed to serialize a signature value", Cause: err}
	}
	return string(canonical), nil
}

func stringifyJSONNumber(number json.Number) (string, error) {
	raw := number.String()
	decoder := json.NewDecoder(strings.NewReader(raw))
	decoder.UseNumber()
	var decoded any
	if err := decoder.Decode(&decoded); err != nil {
		return "", &ClientError{Message: "Failed to serialize a signature value", Cause: err}
	}
	if _, ok := decoded.(json.Number); !ok {
		return "", &ClientError{Message: "Failed to serialize a signature value"}
	}
	if err := ensureJSONEOF(decoder); err != nil {
		return "", &ClientError{Message: "Failed to serialize a signature value", Cause: err}
	}
	if !strings.ContainsAny(raw, ".eE") {
		integer, ok := new(big.Int).SetString(raw, 10)
		if !ok {
			return "", &ClientError{Message: "Failed to serialize a signature value"}
		}
		if integer.Sign() == 0 {
			return "0", nil
		}
		return raw, nil
	}
	canonical, err := CanonicalizeJSON([]byte(raw))
	if err != nil {
		return "", &ClientError{Message: "Failed to serialize a signature value", Cause: err}
	}
	return string(canonical), nil
}

func validateCanonicalJSONNumbers(raw []byte) error {
	decoder := json.NewDecoder(bytes.NewReader(raw))
	decoder.UseNumber()
	var value any
	if err := decoder.Decode(&value); err != nil {
		return err
	}
	if err := ensureJSONEOF(decoder); err != nil {
		return err
	}
	return walkCanonicalJSONNumbers(value)
}

func walkCanonicalJSONNumbers(value any) error {
	switch typed := value.(type) {
	case json.Number:
		raw := typed.String()
		if !strings.ContainsAny(raw, ".eE") {
			integer, ok := new(big.Int).SetString(raw, 10)
			if !ok {
				return fmt.Errorf("invalid JSON number %q", raw)
			}
			limit := big.NewInt(maxSafeProjectID)
			if new(big.Int).Abs(integer).Cmp(limit) > 0 {
				return fmt.Errorf("integer %s is outside the RFC 8785 safe integer range", raw)
			}
			return nil
		}
		parsed, err := strconv.ParseFloat(raw, 64)
		if err != nil || math.IsNaN(parsed) || math.IsInf(parsed, 0) {
			return fmt.Errorf("number %q cannot be represented by RFC 8785", raw)
		}
	case []any:
		for _, item := range typed {
			if err := walkCanonicalJSONNumbers(item); err != nil {
				return err
			}
		}
	case map[string]any:
		for _, item := range typed {
			if err := walkCanonicalJSONNumbers(item); err != nil {
				return err
			}
		}
	default:
		if value == nil {
			return nil
		}
	}
	return nil
}

// SignTeamRequest returns the Team API HMAC-SHA256 signature.
func SignTeamRequest(
	path string,
	timestamp int64,
	nonce string,
	canonicalBody []byte,
	accessSecret string,
) (string, error) {
	if !strings.HasPrefix(path, "/") {
		return "", &ClientError{Message: "Team API signing path must start with '/'"}
	}
	if timestamp <= 0 {
		return "", &ClientError{Message: "Team API timestamp must be a positive integer"}
	}
	if len(nonce) < 16 || len(nonce) > 64 {
		return "", &ClientError{Message: "Team API nonce must contain 16 to 64 characters"}
	}
	if strings.TrimSpace(accessSecret) == "" {
		return "", &ClientError{Message: "Access Secret is required"}
	}
	signingText := fmt.Sprintf("%s\n%d\n%s\n%s", path, timestamp, nonce, canonicalBody)
	mac := hmac.New(sha256.New, []byte(accessSecret))
	_, _ = mac.Write([]byte(signingText))
	return hex.EncodeToString(mac.Sum(nil)), nil
}

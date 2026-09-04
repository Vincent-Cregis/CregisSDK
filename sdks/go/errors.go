package cregis

import "fmt"

// ClientError reports invalid configuration, serialization, or network errors.
type ClientError struct {
	Message string
	Cause   error
}

func (err *ClientError) Error() string {
	if err.Cause == nil {
		return err.Message
	}
	return fmt.Sprintf("%s: %v", err.Message, err.Cause)
}

// Unwrap returns the underlying client or network error.
func (err *ClientError) Unwrap() error { return err.Cause }

// HTTPError reports a non-2xx HTTP response.
type HTTPError struct {
	StatusCode   int
	Status       string
	ResponseBody string
}

func (err *HTTPError) Error() string {
	return fmt.Sprintf("Cregis HTTP request failed with %s", err.Status)
}

// APIError reports an error code returned in a successful HTTP response.
type APIError struct {
	Code    string
	Message string
}

func (err *APIError) Error() string {
	return fmt.Sprintf("Cregis API error %s: %s", err.Code, err.Message)
}

// ContractError reports a request or response that violates the OpenAPI contract.
type ContractError struct {
	Context string
	Cause   error
}

func (err *ContractError) Error() string {
	return fmt.Sprintf("Cregis contract error in %s: %v", err.Context, err.Cause)
}

// Unwrap returns the underlying encoding or validation error.
func (err *ContractError) Unwrap() error { return err.Cause }

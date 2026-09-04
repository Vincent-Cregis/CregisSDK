package cregis

import (
	"net/http"
	"time"
)

const (
	// Version is the SDK semantic version.
	Version                = "0.1.0"
	defaultTimeout         = 30 * time.Second
	maxSafeProjectID int64 = 9_007_199_254_740_991
)

// ProjectConfig configures a Payment Engine or WaaS client.
type ProjectConfig struct {
	ProjectID  int64
	APIKey     string
	BaseURL    string
	Timeout    time.Duration
	HTTPClient *http.Client
}

// TeamConfig configures a Team API client.
type TeamConfig struct {
	AccessKey    string
	AccessSecret string
	BaseURL      string
	Timeout      time.Duration
	HTTPClient   *http.Client
}

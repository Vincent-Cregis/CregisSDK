package cregis

// Ptr returns a pointer to value, including generated named enum values.
func Ptr[T any](value T) *T { return &value }

// String returns a pointer to value for optional generated fields.
func String(value string) *string { return Ptr(value) }

// Bool returns a pointer to value for optional generated fields.
func Bool(value bool) *bool { return Ptr(value) }

// Int returns a pointer to value for optional generated fields.
func Int(value int) *int { return Ptr(value) }

// Int32 returns a pointer to value for optional generated fields.
func Int32(value int32) *int32 { return Ptr(value) }

// Int64 returns a pointer to value for optional generated fields.
func Int64(value int64) *int64 { return Ptr(value) }

// Float32 returns a pointer to value for optional generated fields.
func Float32(value float32) *float32 { return Ptr(value) }

// Float64 returns a pointer to value for optional generated fields.
func Float64(value float64) *float64 { return Ptr(value) }

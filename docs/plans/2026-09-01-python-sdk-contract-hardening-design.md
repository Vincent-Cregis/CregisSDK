# Python SDK contract hardening design

## Goal

Close the contract and packaging gaps found during the first Python SDK review
without changing the synchronous public client surface or weakening declared
field-type validation.

## Model policy

Generated request models remain strict and reject undocumented input fields so
that caller mistakes fail before a signed request is sent. Generated response
and webhook models remain strict for declared fields but ignore unknown fields.
This preserves forward compatibility when Cregis adds response data while
keeping documented wire types enforceable. The generator determines a model's
direction from the prepared operation and webhook metadata and fails if one
model name is reused with incompatible directions.

Every OpenAPI `integer` with `format: int64` receives the signed 64-bit bounds
`-9223372036854775808` through `9223372036854775807`. The bounds are injected by
the reproducible generation pipeline and checked by tests; generated files are
never patched manually.

## Errors and packaging

Malformed successful-response envelopes use `CregisContractError`, matching
the documented exception hierarchy. Pydantic raises `ValidationError` while a
request model is being constructed, before a client method can wrap it, so the
README documents that behavior explicitly.

The Python distribution includes the repository Apache-2.0 license in built
artifacts. Personal repository URLs are removed until an official organization
repository URL is available, and installation documentation distinguishes
local development installation from a future PyPI release.

## Verification

Regression tests cover both int64 boundaries, unknown-field behavior for each
model direction, envelope error categories, and wheel license contents. The
existing 23-operation mock suite, signing vectors, webhooks, lint, typing,
Python 3.9 compatibility, deterministic code generation, and wheel import check
remain required. Real mutating Sandbox tests stay behind the existing explicit
opt-in and are not run as part of this hardening change.

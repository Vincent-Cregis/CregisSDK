# Cregis Java SDK Tasks

The canonical Payment Engine, WaaS, and Team API OpenAPI files are the source
of truth for this checklist.

## OpenAPI alignment

- [x] Payment Engine operations: 2/2
- [x] WaaS operations: 15/15
- [x] Team API operations: 6/6
- [x] Payment Engine webhooks: 1/1
- [x] WaaS webhooks: 4/4
- [x] Project body signing with numeric `pid`
- [x] Team RFC 8785 + HMAC-SHA256 header signing
- [x] Explicit project- or team-specific Base URL

## Verification

- [x] Unit tests do not require API credentials
- [x] Every OpenAPI operation path has a local contract test
- [x] Project and Team signature test vectors
- [x] Nested Payment response and webhook fixtures
- [x] State-changing Sandbox tests require explicit opt-in
- [x] Add local OpenAPI-to-Java operation drift check
- [x] Test drift-check behavior in CI
- [ ] Add sanitized backend callback signature fixture
- [x] Normalize inline schemas for stable generated Java model names
- [x] Generate and commit operation models from the prepared specifications
- [x] Reject stale files, handwritten DTO duplicates, and leaked auth fields

## Release readiness

- [ ] Complete public Javadoc
- [x] Add Maven Central metadata and release plugins
- [x] Add source and Javadoc artifacts
- [x] Add repository license file
- [ ] Publish a Java beta before `1.0.0` (postponed)

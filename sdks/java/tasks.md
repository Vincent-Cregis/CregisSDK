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
- [ ] Add a CI OpenAPI-to-Java drift check when generation is introduced

## Release readiness

- [ ] Complete public Javadoc
- [ ] Add Maven Central metadata and release plugins
- [ ] Add source and Javadoc artifacts
- [ ] Add repository license file
- [ ] Publish a Java beta before `1.0.0`

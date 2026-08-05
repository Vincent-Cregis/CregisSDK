# Cregis SDK automation design

## Goal

Maintain Java, TypeScript/Node.js, Python, Go, and PHP SDKs from the canonical OpenAPI specifications while preserving Cregis-specific signing, webhook handling, error handling, and developer-friendly clients.

## Repository boundary

`0xcregis/cregis-developer-docs` remains the only source of truth for the Payment Engine, WaaS, and Team API OpenAPI specifications. This SDK repository does not commit a second copy of those specifications.

The SDK repository contains all language SDKs and the shared generation tooling. A generation run receives a documentation repository commit, downloads the three specifications from that exact commit into a temporary directory, validates them, generates code, runs tests, and opens a reviewable pull request. The pull request records the source commit but does not include the downloaded specifications.

## Layout

- `codegen/configs`: per-language generator configuration
- `codegen/templates`: Cregis-specific templates, added only when configuration is insufficient
- `codegen/scripts`: fetch, validate, generate, diff, and test commands
- `sdks/<language>`: independently buildable and releasable SDK packages
- `.github/workflows`: cross-repository update and release automation

## Code ownership

Generated API operations and data models live in clearly marked generated packages and are never edited manually. Authentication, signing, webhook verification, exceptions, retries, and public convenience clients remain handwritten runtime code. Regeneration replaces only generated paths.

## Update flow

1. An OpenAPI change is merged into the documentation repository.
2. The documentation repository sends its commit identifier to this repository.
3. This repository downloads and validates the specifications from that commit.
4. Compatibility checks classify breaking and non-breaking changes.
5. SDK code is regenerated and compiled.
6. Unit, installation, and contract tests run.
7. Automation opens one SDK update pull request for human review.
8. SDK packages are released independently after the pull request is merged.

## Delivery order

The existing Java SDK is migrated first without structural changes. After its build and tests are stable, Java becomes the first generation proof of concept. TypeScript/Node.js, Python, Go, and PHP follow only after the Java update flow is repeatable.

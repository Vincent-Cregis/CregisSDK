# Cregis SDKs

This repository contains the official Cregis SDKs and the tooling used to generate them from the canonical OpenAPI specifications in `0xcregis/cregis-developer-docs`.

## Repository layout

- `codegen/`: generator configuration and scripts
- `sdks/java/`: Java SDK
- `sdks/typescript/`: TypeScript and Node.js SDK
- `sdks/python/`: Python SDK
- `sdks/go/`: Go SDK
- `sdks/php/`: PHP SDK

The OpenAPI specifications are not copied into this repository. Generation jobs fetch an exact commit from the documentation repository and record that source commit with the generated change.

When canonical specifications change, the documentation repository dispatches
their exact commit to `openapi-sdk-update.yml`. The workflow regenerates Java
and TypeScript, runs both SDK test suites, and opens a reviewable pull request.
See the [OpenAPI automation runbook](docs/releasing/openapi-automation.md) for
the two cross-repository secrets and recovery steps.

## Current status

The Java SDK covers all 23 Payment Engine, WaaS, and Team API operations with
local contract tests and Sandbox coverage. The TypeScript/Node.js SDK now uses
the same API surface and a reproducible OpenAPI model-generation pipeline.

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).

## Releasing

Java SDK maintainers should follow the [Java release runbook](docs/releasing/java.md).

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

## Current status

The Java SDK is preparing its `1.0.0-rc.1` release candidate. Its 23 API operations are aligned with the canonical Payment Engine, WaaS, and Team API contracts, with local contract tests and Sandbox coverage in place. Automated generation will be introduced after this Java release baseline is stable.

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).

## Releasing

Java SDK maintainers should follow the [Java release runbook](docs/releasing/java.md).

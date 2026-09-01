# OpenAPI SDK update automation

## Repository setup

Configure these GitHub Actions secrets once:

- In `0xcregis/cregis-developer-docs`, set `CREGIS_SDK_DISPATCH_TOKEN` to a
  fine-grained token that can send a repository dispatch to
  `Vincent-Cregis/CregisSDK`.
- In `Vincent-Cregis/CregisSDK`, set `CREGIS_DOCS_READ_TOKEN` to a fine-grained
  token with read access to the private documentation repository.

The SDK repository must also allow GitHub Actions to create pull requests.
Never put either token in `.env`, source code, workflow text, or generated
files.

## Normal update

1. Merge reviewed changes under `api-sources/specs` in the documentation
   repository.
2. The documentation workflow sends the exact source commit to the SDK
   repository.
3. The SDK workflow regenerates Java, TypeScript, and Python using the pinned generator.
4. It runs generator tests plus the Java, TypeScript, and Python verification suites.
5. If generated output changed, it updates a branch named
   `automation/openapi-<source-sha>` and opens a pull request.

The downloaded specifications live only in the ignored `.openapi-source`
working directory. Generated lock manifests record each source file hash.

## Manual recovery

Run the `Update SDKs from OpenAPI` workflow manually and supply a documentation
commit or branch as `spec_ref`. Re-running the same source commit updates the
same pull-request branch. If there is no generated diff, the workflow exits
without creating an empty pull request.

Before merging, review model and runtime-schema changes, confirm the source
commit, and require the Java, TypeScript, and Python CI checks. Package
publication is a separate, explicitly approved workflow.

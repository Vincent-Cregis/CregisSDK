# SDK code generation

The canonical OpenAPI files remain in the separate
`cregis-developer-docs` repository. This repository does not keep a second
copy.

## Check Java operation drift

Pass the local canonical-spec directory explicitly:

```bash
./codegen/scripts/check-java-openapi.py \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

The command compares all HTTP methods, paths, and OpenAPI `operationId` values
with `codegen/configs/java-operations.json`, then confirms that the mapped Java
client method still calls the expected path. It currently expects 2 Payment,
15 WaaS, and 6 Team operations.

The checker does not download or copy specifications. Its own tests run in CI:

```bash
python3 -m unittest discover -s codegen/tests -v
```

## Generate disposable Java models

The staging generator uses OpenAPI Generator 7.19.0 through an immutable Docker
image digest. It never writes into `sdks/java` and requires an empty output
directory:

```bash
output_dir="$(mktemp -d)"
./codegen/scripts/generate-java-staging.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs \
  --output-dir "$output_dir"
```

The output contains models only, under separate Payment, WaaS, and Team
packages. It is inspection output, not production source code.

The current specifications use inline request and response schemas and include
authentication fields in public request bodies. Standard generated names such
as `CreateOrder200ResponseAllOfData` are not suitable as a stable SDK API, and
users must not be asked to populate `pid`, `nonce`, `timestamp`, or `sign`.
Before generated models replace handwritten models, the generation preparation
step must give inline schemas stable semantic names and remove SDK-managed
authentication fields from outbound request models.

Handwritten HTTP, signing, exception, and webhook code must never be placed in
or copied into a generated output directory.

# SDK code generation

The canonical Payment Engine, WaaS, and Team OpenAPI files remain in the
separate `cregis-developer-docs` repository. This repository deliberately does
not keep a second copy of those specifications.

## Java model pipeline

Run the pipeline from the root of this repository and point it at a local
checkout of the canonical specifications:

```bash
./codegen/scripts/generate-java-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

The command performs these steps:

1. Verifies that all OpenAPI operations are mapped in
   `codegen/configs/java-operations.json` and `codegen/configs/java-models.json`.
2. Builds disposable prepared specifications in a temporary directory.
3. Removes SDK-managed `pid`, `nonce`, `timestamp`, and `sign` fields from
   Payment/WaaS request models.
4. Unwraps response `data` objects and assigns stable names to inline schemas.
5. Generates models with the digest-pinned OpenAPI Generator 7.19.0 image.
6. Replaces `sdks/java/src/generated/java` and writes
   `codegen/manifests/java-models.lock.json`.
7. Checks that the generated boundary has no missing files, duplicate
   handwritten operation DTOs, or leaked SDK-managed request fields.

The prepared specifications are always temporary. The generated Java models
and lock manifest are committed so SDK consumers do not need Docker or the
documentation repository.

Do not edit `sdks/java/src/generated/java` by hand. Change the canonical
OpenAPI file or the stable-name mapping and regenerate instead.

## Verify reproducibility

Use `--check` when the committed output must not be changed:

```bash
./codegen/scripts/generate-java-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs \
  --check
```

This regenerates into a temporary directory and fails if either the Java files
or the lock manifest differ from the committed result.

The following faster check does not require the OpenAPI files or Docker. CI
runs it to validate the committed generated/handwritten boundary:

```bash
./codegen/scripts/check-java-generated-models.py
```

## Check operation drift

Compare the canonical operations and Java Client paths:

```bash
./codegen/scripts/check-java-openapi.py \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

It currently expects 2 Payment, 15 WaaS, and 6 Team operations.

Run all code-generation tool tests with:

```bash
python3 -m unittest discover -s codegen/tests -v
```

## Handwritten boundary

Only operation request/response models are generated. HTTP transport, signing,
exceptions, the public Clients, Payment/WaaS webhook models, and webhook
handlers remain handwritten. Generated code must never overwrite those files.

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
   `codegen/configs/openapi-operations.json` and `codegen/configs/openapi-models.json`.
   Language-specific public method names live in `java-overrides.json`,
   `typescript-overrides.json`, `python-overrides.json`, and `go-overrides.json`, so each SDK can
   follow its own conventions.
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

## TypeScript model pipeline

The TypeScript SDK uses the same operation and stable-model mappings as Java,
but generates pure TypeScript interfaces, enum values, operation metadata,
compact runtime schemas, and webhook wire contracts. HTTP transport,
authentication, public clients, and webhook signature verification remain
handwritten. Generation first checks the complete canonical operation inventory,
so a newly added or removed OpenAPI operation cannot pass unnoticed.

```bash
./codegen/scripts/generate-typescript-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

Use `--check` for a byte-for-byte reproducibility check. The committed output
is under `sdks/typescript/src/generated`, and its source hashes and generator
identity are recorded in `codegen/manifests/typescript-models.lock.json`.

The fast local checks do not require Docker or a specification checkout:

```bash
./codegen/scripts/check-typescript-generated-models.py
./codegen/scripts/check-typescript-client-surface.py
```

The following faster Java check does not require the OpenAPI files or Docker:

```bash
./codegen/scripts/check-java-generated-models.py
```

## Python model pipeline

The synchronous Python SDK generates strict Pydantic v2 request, response, and
webhook models plus operation metadata. Request models reject undocumented
fields; response and webhook models ignore new fields while continuing to
validate every declared field and type. OpenAPI `int64` values receive signed
64-bit bounds before generation. HTTP transport, authentication, public
clients, exceptions, and webhook verification remain handwritten.

```bash
./codegen/scripts/generate-python-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

The committed output is under `sdks/python/src/cregis/generated`; source hashes
and the digest-pinned generator identity are recorded in
`codegen/manifests/python-models.lock.json`, together with each model's request
or output policy. Use `--check` for byte-for-byte reproducibility. The fast
local boundary check is:

```bash
./codegen/scripts/check-python-generated-models.py
```

## Check operation drift

Compare the canonical operations and Java Client paths:

```bash
./codegen/scripts/check-java-openapi.py \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

It currently expects 2 Payment, 15 WaaS, and 6 Team operations.

## Go model pipeline

The Go SDK generates standard-library struct models, named enum types and
constants, recursive request/response/webhook validation, strict request JSON
decoders, public model and enum aliases, and all 23 typed client methods.
Responses validate required fields, declared JSON types, and documented
constraints while ignoring future response fields.
HTTP transport, both signing schemes, errors, and webhook verification remain
handwritten.

```bash
./codegen/scripts/generate-go-models.sh \
  --spec-dir ../cregis-developer-docs/api-sources/specs
```

The committed output is under `sdks/go/generated`, plus
`sdks/go/models_aliases.gen.go` and `sdks/go/clients.gen.go`. Source hashes and
the renderer identity are recorded in `codegen/manifests/go-models.lock.json`.
Use `--check` for byte-for-byte reproducibility. The fast boundary check is:

```bash
./codegen/scripts/check-go-generated-models.py
```

Run all code-generation tool tests with:

```bash
python3 -m unittest discover -s codegen/tests -v
```

## Handwritten boundary

Operation request/response models, runtime schemas, operation metadata, and
Payment/WaaS webhook wire models are generated. HTTP transport, signing,
exceptions, public clients, and webhook verification handlers remain
handwritten. Generated code must never overwrite those files.

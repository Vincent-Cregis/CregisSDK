# Java SDK cleanup design

## Scope

Remove the disposable raw Java generation path now that the prepared,
reproducible model pipeline is established. Delete its CI and documentation
references. Remove placeholder files from non-empty codegen directories and
drop the unused empty template directory until custom templates are actually
introduced.

## Dependency boundary

Declare libraries that production source imports directly instead of relying
on transitive dependencies:

- Jackson annotations and core alongside databind
- Okio JVM alongside OkHttp

Remove optional JetBrains nullability annotations from interceptor method
signatures instead of declaring another direct API dependency. Keep
`javax.annotation-api` and JSR-305 because committed OpenAPI-generated source
uses their annotations. Replace the JUnit aggregate with explicit API and
engine test dependencies.

This cleanup changes neither SDK behavior nor public request, response, client,
or webhook APIs.

## Verification

Run codegen tests and the generated-model boundary check, Maven dependency
analysis, a clean Java build, Maven and Gradle consumer smoke tests, and all
Sandbox integration tests before pushing the commits to `origin/main`.

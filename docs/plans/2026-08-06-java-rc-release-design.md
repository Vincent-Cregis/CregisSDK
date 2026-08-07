# Java SDK 1.0.0-rc.1 Release Design

## Goal

Turn the OpenAPI-aligned and Sandbox-verified Java SDK into an installable release candidate. The release must be consumable from both Maven and Gradle, remain compatible with Java 11, and have a repeatable GitHub Actions publishing path.

The first release candidate uses these coordinates:

```text
com.cregis:cregis-sdk-java:1.0.0-rc.1
```

This is a new artifact and does not replace the legacy Maven Central artifacts `cregis-java-sdk`, `cregis-sdk-core`, or `cregis-sdk-spring-boot-starter`, whose latest published version is `1.1.0`.

## Package and legal metadata

The repository will use Apache License 2.0, matching the legacy official Java SDK. The Maven POM will include the project name, description, project URL, license, organization, developer, SCM, issue tracker, and Java 11 compatibility metadata required for a public library.

Every release build will attach the main JAR, sources JAR, JavaDoc JAR, POM, and signatures. Archive timestamps will be pinned for reproducible release artifacts.

## Verification

Normal CI will run the Java SDK build on Java 11, 17, and 21. Java 11 is the compatibility floor; later JDKs prove that applications can build the SDK on supported modern runtimes.

An independent Maven consumer and an independent Gradle consumer will compile against the artifact installed in the local Maven repository. These projects must use only public SDK APIs and must not rely on reactor source paths. This catches missing runtime dependencies, invalid POM metadata, accidental test-only dependencies, and packaging mistakes.

The existing 20 local tests, 23 Sandbox operation tests, and callback contract tests remain the behavioral evidence. Sandbox tests stay opt-in and are not run during a public release.

## Publishing

Git tags use the monorepo-safe format `java-v<version>`, beginning with `java-v1.0.0-rc.1`. A release workflow will reject tags that do not match the POM version, run the full local build and consumer smoke tests, sign artifacts with the Maven GPG plugin, and upload through the Sonatype Central Portal Maven plugin.

Publishing requires four GitHub repository secrets:

- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_TOKEN`
- `MAVEN_GPG_KEY`
- `MAVEN_GPG_PASSPHRASE`

The workflow will be committed and pushed before any tag is created. If the secrets or namespace permission are missing, the repository remains release-ready but no irreversible release tag is created.

## Completion criteria

- Apache-2.0 license and complete POM metadata are present.
- `mvn clean verify` passes on Java 11, 17, and 21.
- Main, sources, JavaDoc, and POM artifacts are produced.
- Maven and Gradle consumer projects compile against `1.0.0-rc.1` from Maven Local.
- CI and release workflows validate successfully after push.
- The release tag and Central publication occur only after all required secrets are available.

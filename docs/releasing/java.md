# Java SDK release runbook

This runbook publishes `com.cregis:cregis-sdk-java` to Maven Central and creates a matching GitHub release. Maven Central versions are immutable, so never reuse a version after it has been published.

## One-time setup

1. Sign in to the [Maven Central Publisher Portal](https://central.sonatype.com) and confirm that the account can publish the `com.cregis` namespace.
2. Generate a Maven Central user token. Store the generated username and token as these GitHub Actions repository secrets:
   - `MAVEN_CENTRAL_USERNAME`
   - `MAVEN_CENTRAL_TOKEN`
3. Create a company-controlled signing key with a strong passphrase. Replace the example identity with a real Cregis release identity:

   ```bash
   gpg --quick-generate-key \
     "Cregis SDK Release <release-email@cregis.com>" \
     rsa3072 sign 2y
   gpg --list-secret-keys --keyid-format=long
   ```

4. Publish the public key so Maven Central and SDK users can verify signatures:

   ```bash
   gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_PRIMARY_KEY_FINGERPRINT
   ```

5. Send the private key directly to GitHub Actions without writing it into this repository:

   ```bash
   gpg --armor --export-secret-keys YOUR_PRIMARY_KEY_FINGERPRINT | \
     gh secret set MAVEN_GPG_KEY
   gh secret set MAVEN_GPG_PASSPHRASE
   ```

6. Add the Maven Central credentials. Each command prompts securely for the value:

   ```bash
   gh secret set MAVEN_CENTRAL_USERNAME
   gh secret set MAVEN_CENTRAL_TOKEN
   gh secret list
   ```

Never commit, paste into an issue, or send any private key, passphrase, or Maven Central token through chat.

## Release checklist

1. Confirm `main` is clean, synchronized, and green in GitHub Actions.
2. Confirm the Maven version in `sdks/java/pom.xml`, installation examples, consumer smoke tests, and `CHANGELOG.md` all match.
3. For a release candidate, verify the version references and run the full local release build without publishing. The default GPG signer uses the private key in the local GPG keyring:

   ```bash
   ./scripts/verify-java-version.sh
   mvn -f sdks/java/pom.xml -Prelease clean verify
   ```

4. Create and push an annotated tag whose value exactly matches the Maven version:

   ```bash
   release_version="$(mvn --quiet -f sdks/java/pom.xml \
     help:evaluate -Dexpression=project.version -DforceStdout)"
   git tag -a "java-v${release_version}" \
     -m "Cregis Java SDK ${release_version}"
   git push origin "java-v${release_version}"
   ```

5. Watch the `Publish Java SDK` workflow. It checks the tag/version match, signs four Maven artifacts, publishes them through the Central Portal, waits for publication, then creates a GitHub prerelease with the binary, source, and JavaDoc jars.
6. Verify the published package from a clean consumer project:

   ```bash
   release_version="$(mvn --quiet -f sdks/java/pom.xml \
     help:evaluate -Dexpression=project.version -DforceStdout)"
   mvn dependency:get \
     -Dartifact="com.cregis:cregis-sdk-java:${release_version}"
   ```

## Failure handling

- If the workflow fails before Maven Central reports the deployment as published, fix the cause and rerun the failed workflow.
- If the version has reached Maven Central, do not delete or overwrite it. Fix the problem, change every version reference to the next release candidate, update `CHANGELOG.md`, and publish a new tag.
- Do not move or recreate a published release tag.

Current requirements are documented by Sonatype in [Maven Central publishing requirements](https://central.sonatype.org/publish/requirements/), [PGP signing guidance](https://central.sonatype.org/publish/requirements/gpg/), and the [Central Publishing Maven plugin guide](https://central.sonatype.org/publish/publish-portal-maven/).

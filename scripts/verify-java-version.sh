#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
pom_path="${repo_root}/sdks/java/pom.xml"
sdk_version="$(mvn --quiet -f "${pom_path}" help:evaluate -Dexpression=project.version -DforceStdout)"
failed=0

require_text() {
  local relative_path="$1"
  local expected_text="$2"
  local absolute_path="${repo_root}/${relative_path}"

  if ! grep -Fq -- "${expected_text}" "${absolute_path}"; then
    echo "Version ${sdk_version} is missing from ${relative_path}: ${expected_text}" >&2
    failed=1
  fi
}

require_text "README.md" "\`${sdk_version}\`"
require_text "CHANGELOG.md" "## ${sdk_version}"
require_text "sdks/java/README.md" "<version>${sdk_version}</version>"
require_text "sdks/java/README.md" "implementation(\"com.cregis:cregis-sdk-java:${sdk_version}\")"
require_text "sdks/java/smoke-tests/maven-consumer/pom.xml" "<cregis.sdk.version>${sdk_version}</cregis.sdk.version>"
require_text "sdks/java/smoke-tests/gradle-consumer/build.gradle.kts" "orElse(\"${sdk_version}\")"

if [[ "${failed}" -ne 0 ]]; then
  exit 1
fi

echo "All Java SDK version references match ${sdk_version}."

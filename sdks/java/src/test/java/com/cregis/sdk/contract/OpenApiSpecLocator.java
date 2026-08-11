package com.cregis.sdk.contract;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Locates the canonical specs without copying them into the SDK repository. */
public final class OpenApiSpecLocator {

    private OpenApiSpecLocator() {
    }

    public static Path locateSpecDirectory() {
        String configured = firstNonBlank(
                System.getProperty("cregis.openapi.specDir"),
                System.getenv("CREGIS_OPENAPI_SPEC_DIR"));
        if (configured != null) {
            Path path = Paths.get(configured).toAbsolutePath().normalize();
            if (isSpecDirectory(path)) {
                return path;
            }
            throw new IllegalStateException("Configured OpenAPI spec directory is incomplete: " + path);
        }

        Path current = Paths.get("").toAbsolutePath().normalize();
        while (current != null) {
            Path docsCheckout = current.resolve("cregis-developer-docs/api-sources/specs");
            if (isSpecDirectory(docsCheckout)) {
                return docsCheckout;
            }
            Path direct = current.resolve("api-sources/specs");
            if (isSpecDirectory(direct)) {
                return direct;
            }
            current = current.getParent();
        }

        throw new IllegalStateException(
                "Canonical OpenAPI specs not found; set CREGIS_OPENAPI_SPEC_DIR");
    }

    public static Path locateSpec(String apiName) {
        String fileName;
        switch (apiName) {
            case "payment":
                fileName = "payment-engine-api.json";
                break;
            case "waas":
                fileName = "waas-api.json";
                break;
            case "team":
                fileName = "team-api.json";
                break;
            default:
                throw new IllegalArgumentException("Unknown API: " + apiName);
        }
        return locateSpecDirectory().resolve(fileName);
    }

    private static boolean isSpecDirectory(Path path) {
        return Files.isRegularFile(path.resolve("payment-engine-api.json"))
                && Files.isRegularFile(path.resolve("waas-api.json"))
                && Files.isRegularFile(path.resolve("team-api.json"));
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.trim().isEmpty()) {
            return first.trim();
        }
        if (second != null && !second.trim().isEmpty()) {
            return second.trim();
        }
        return null;
    }
}

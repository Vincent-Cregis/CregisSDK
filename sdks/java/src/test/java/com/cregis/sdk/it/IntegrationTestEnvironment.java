package com.cregis.sdk.it;

import io.github.cdimascio.dotenv.Dotenv;

final class IntegrationTestEnvironment {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private IntegrationTestEnvironment() {
    }

    static String get(String key) {
        String environmentValue = System.getenv(key);
        if (isPresent(environmentValue)) {
            return environmentValue;
        }

        String dotenvValue = DOTENV.get(key);
        return isPresent(dotenvValue) ? dotenvValue : null;
    }

    static boolean isTrue(String key) {
        return Boolean.parseBoolean(get(key));
    }

    static boolean allPresent(String... keys) {
        for (String key : keys) {
            if (get(key) == null) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPresent(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

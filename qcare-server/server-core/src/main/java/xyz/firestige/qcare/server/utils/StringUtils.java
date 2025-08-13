package xyz.firestige.qcare.server.utils;

import com.google.common.base.Splitter;

public final class StringUtils {
    public static Map<String, String> parseQueryString(String query) {
        return Splitter.on('&')
                .trimResults()
                .withKeyValueSeparator("=")
                .split(query);
    }

    private StringUtils() {
        // Prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}

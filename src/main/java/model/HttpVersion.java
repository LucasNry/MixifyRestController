package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HttpVersion {
    ONE("HTTP/1.0"),
    ONE_DOT_ONE("HTTP/1.1"),
    TWO("HTTP/2.0"),
    THREE("HTTP/3.0");

    private static final String VERSION_NOT_FOUND_ERROR_TEMPLATE = "No version was found for String [%s]";

    @Getter
    private String stringValue;

    public static HttpVersion fromString(String stringValue) {
        for (HttpVersion httpVersion : HttpVersion.values()) {
            if (httpVersion.stringValue.equals(stringValue)) {
                return httpVersion;
            }
        }

        throw new IllegalArgumentException(String.format(VERSION_NOT_FOUND_ERROR_TEMPLATE, stringValue));
    }
}

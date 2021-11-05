package model;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@AllArgsConstructor
public class Headers {
    private static final String HEADER_TEMPLATE = "%s: %s";
    private static final String CRLF = "\r\n";

    public static final String DATE = "Date";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String KEEP_ALIVE = "Keep-Alive";
    public static final String USER_AGENT = "User-Agent";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    private static final String UNEVEN_NUMBER_OF_ARGUMENTS_ERROR_TEPLATE = "The number of parameters must be even, consisting of key/value pairs. [Number of parameters: [%s]]";


    private Map<String, String> headerMap;

    public Headers() {
        this(new HashMap<String, String>(){{
            put(CONTENT_TYPE, "application/json");
            put(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }});
    }

    public Headers(String... keyOrValue) {
        this();
        int numberOfParameters = keyOrValue.length;
        if (numberOfParameters % 2 != 0) {
            throw new IllegalArgumentException(String.format(UNEVEN_NUMBER_OF_ARGUMENTS_ERROR_TEPLATE, numberOfParameters));
        }

        for (int i = 0; i < numberOfParameters; i += 2) {
            headerMap.put(keyOrValue[i], keyOrValue[i + 1]);
        }
    }

    public String getHeader(String headerName) {
        return headerMap.getOrDefault(headerName, null);
    }

    public void addHeader(String headerName, String headerValue) {
        headerMap.put(headerName, headerValue);
    }

    public boolean hasHeader(String headerKey) {
        return headerMap.containsKey(headerKey);
    }

    public int getSize() {
        return headerMap.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Entry<String, String> entry : headerMap.entrySet()) {
            sb
                    .append(String.format(HEADER_TEMPLATE, entry.getKey(), entry.getValue()))
                    .append(CRLF);
        }

        return sb.toString();
    }
}

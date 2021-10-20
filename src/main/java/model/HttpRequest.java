package model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@AllArgsConstructor(access=AccessLevel.PACKAGE)
public class HttpRequest {

    private static final String CRLF = "\r\n";
    private static final String INITIAL_LINE_TEMPLATE = "%s %s %s";
    private static final String PATH_TEMPLATE = "%s?%s";

    private static final String CRLF_REGEX = "\\r\\n";
    private static final String INITIAL_LINE_SEPARATOR = " ";
    private static final String PATH_SEPARATOR = "\\?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String PARAMETER_VALUE_SEPARATOR = "=";
    private static final String HEADER_SEPARATOR = ":";

    private Method method;

    private String path;

    private QueryParameters queryParameters;

    private HttpVersion httpVersion;

    private Headers headers;

    private String body;

    public static HttpRequest fromString(String requestString) {
        int index = 0;
        String[] requestLines = requestString.split(CRLF_REGEX);

        String[] initialLine = parseInitialLine(requestLines[index]);
        Method method = Method.fromString(initialLine[0]);
        HttpVersion version = HttpVersion.fromString(initialLine[2]);

        String[] pathDivided = initialLine[1].split(PATH_SEPARATOR);
        String path = pathDivided[0];
        QueryParameters queryParameters = null;

        if (pathDivided.length > 1) {
            queryParameters = parseQueryParameters(pathDivided[1]);
        }

        index++;

        Headers headers = parseHeaders(requestLines, index);

        // Incrementing by 1 to skip to the line after the last header and incrementing once more to skip the empty separator line
        index = headers.getSize() + 2;

        String body = parseBody(requestLines, index);

        return new HttpRequest(
                method,
                path,
                Optional
                        .ofNullable(queryParameters)
                        .orElse(null),
                version,
                headers,
                body
        );
    }

    private static String[] parseInitialLine(String initialLine) {
        return initialLine.split(INITIAL_LINE_SEPARATOR);
    }

    private static QueryParameters parseQueryParameters(String queryParametersString) {
        Map<String, String> queryParameterMap = new HashMap<>();

        for (String parameter : queryParametersString.split(PARAMETER_SEPARATOR)) {
            String[] parameterEntry = parameter.split(PARAMETER_VALUE_SEPARATOR);
            String parameterName = parameterEntry[0];
            String parameterValue = parameterEntry[1];

            queryParameterMap.put(parameterName, parameterValue);
        }

        return new QueryParameters(queryParameterMap);
    }

    private static Headers parseHeaders(String[] requestLines, int index) {
        Map<String, String> headerMap = new HashMap<>();

        while (index < requestLines.length && !requestLines[index].isEmpty()) {
            String[] headerEntry = requestLines[index].split(HEADER_SEPARATOR, 2);
            String headerName = headerEntry[0];
            String headerValue = headerEntry[1].substring(1); // Removes first space

            headerMap.put(headerName, headerValue);
            index++;
        }

        return new Headers(headerMap);
    }

    private static String parseBody(String[] requestLines, int index) {
        if (index > requestLines.length) {
            return "";
        }

        return String.join("\n", Arrays.copyOfRange(requestLines, index, requestLines.length));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append(String.format(INITIAL_LINE_TEMPLATE, method.toString(), String.format(PATH_TEMPLATE, path, queryParameters.toString()), httpVersion.getStringValue()))
                .append(CRLF)
                .append(headers.toString());

        if (!body.isEmpty()) {
            sb
                    .append(CRLF)
                    .append(body);
        }

        return sb.toString();
    }
}

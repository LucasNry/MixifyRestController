package model;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class HttpResponse {
    private static final String CRLF = "\r\n";
    private static final String INITIAL_LINE_TEMPLATE = "%s %s %s";


    private static final String CRLF_REGEX = "\\r\\n";
    private static final String INITIAL_LINE_REGEX = " ";
    private static final String HEADER_REGEX = ":";

    @Builder.Default
    private HttpVersion version = HttpVersion.ONE_DOT_ONE;

    @Builder.Default
    private RequestStatus requestStatus = RequestStatus.OK;

    @Builder.Default
    private Headers headers = new Headers(Collections.emptyMap());

    @Builder.Default
    private String body = "";

    public static HttpResponse fromString(String requestString) {
        int index = 0;
        String[] requestLines = requestString.split(CRLF_REGEX);

        String[] initialLine = parseInitialLine(requestLines[index]);
        HttpVersion version = HttpVersion.fromString(initialLine[0]);
        RequestStatus requestStatus = RequestStatus.fromCode(Integer.parseInt(initialLine[1]));
        index++;

        Headers headers = parseHeaders(requestLines, index);

        // Incrementing by 1 to skip to the line after the last header and incrementing once more to skip empty separator line
        index = headers.getSize() + 2;

        String body = parseBody(requestLines, index);

        return new HttpResponse(version, requestStatus, headers, body);
    }

    private static String[] parseInitialLine(String initialLine) {
        return initialLine.split(INITIAL_LINE_REGEX);
    }

    private static Headers parseHeaders(String[] requestLines, int index) {
        Map<String, String> headerMap = new HashMap<>();

        while (index < requestLines.length && !requestLines[index].isEmpty()) {
            String[] headerEntry = requestLines[index].split(HEADER_REGEX, 2);
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
                .append(String.format(INITIAL_LINE_TEMPLATE, version.getStringValue(), requestStatus.getStatusCode(), requestStatus.getStatusMessage()))
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

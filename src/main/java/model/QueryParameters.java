package model;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@AllArgsConstructor
public class QueryParameters {

    private static final String HEADER_TEMPLATE = "%s=%s";
    private static final String SEPARATOR = "&";

    private static final String UNEVEN_NUMBER_OF_ARGUMENTS_ERROR_TEPLATE = "The number of parameters must be even, consisting of key/value pairs. [Number of parameters: [%s]]";


    private Map<String, String> parameterMap;

    public QueryParameters(String... keyOrValue) {
        int numberOfMethodParameters = keyOrValue.length;
        if (numberOfMethodParameters % 2 != 0) {
            throw new IllegalArgumentException(String.format(UNEVEN_NUMBER_OF_ARGUMENTS_ERROR_TEPLATE, numberOfMethodParameters));
        }

        Map<String, String> parameterMap = new HashMap<>();
        for (int i = 0; i < numberOfMethodParameters; i += 2) {
            parameterMap.put(keyOrValue[i], keyOrValue[i + 1]);
        }

        this.parameterMap = parameterMap;
    }

    public String getParameter(String parameterName) {
        return parameterMap.getOrDefault(parameterName, null);
    }

    public int getSize() {
        return parameterMap.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Entry<String, String> entry : parameterMap.entrySet()) {
            sb
                    .append(String.format(HEADER_TEMPLATE, entry.getKey(), entry.getValue()));

            if (parameterMap.size() > 1) {
                sb.append(SEPARATOR);
            }
        }

        return sb.toString();
    }
}

package controller;

import exceptions.InvalidEndpointException;
import exceptions.InvalidHandlerMethodException;
import model.Headers;
import model.HttpRequest;
import model.HttpResponse;
import model.RequestStatus;

import java.lang.reflect.Method;

import static model.HttpMethod.DELETE;
import static model.HttpMethod.GET;
import static model.HttpMethod.HEAD;
import static model.HttpMethod.POST;
import static model.HttpMethod.PUT;

public class OptionsController extends HttpMethodController {

    @Override
    void setupOperationMap() throws InvalidHandlerMethodException {
    }

    @Override
    Method getOperation(String endpoint) throws InvalidEndpointException {
        return null;
    }

    @Override
    public synchronized HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        Headers requestHeaders = httpRequest.getHeaders();

        String allowedHeaders = requestHeaders.getHeader(Headers.ACCESS_CONTROL_REQUEST_HEADERS);
        String allowedOrigin = requestHeaders.getHeader(Headers.ORIGIN);

        Headers headers = new Headers(
                Headers.ACCESS_CONTROL_ALLOW_METHODS, getMethodString(),
                Headers.ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaders,
                Headers.ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin
            );

        if (exposedHeadersByEndpoint.containsKey(path)) {
            headers.addHeader(Headers.ACCESS_CONTROL_EXPOSE_HEADERS, getExposedHeaders(path));
        }

        return HttpResponse
                .builder()
                .headers(headers)
                .requestStatus(RequestStatus.NO_CONTENT)
                .build();
    }

    private String getMethodString() {
        return new StringBuilder()
                .append(GET.toString())
                .append(SEPARATOR)
                .append(POST.toString())
                .append(SEPARATOR)
                .append(HEAD.toString())
                .append(SEPARATOR)
                .append(PUT.toString())
                .append(SEPARATOR)
                .append(DELETE.toString())
                .toString();
    }
}

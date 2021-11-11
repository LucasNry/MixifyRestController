package controller;

import annotations.QueryParameter;
import annotations.RequestBody;
import annotations.RequestHeader;
import exceptions.InvalidEndpointException;
import exceptions.InvalidHandlerMethodException;
import model.Headers;
import model.HttpRequest;
import model.HttpResponse;
import model.QueryParameters;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public abstract class HttpMethodController {
    protected static final String SEPARATOR = ", ";

    private static final String INVALID_ENDPOINT_ERROR_MESSAGE_TEMPLATE = "No handler method was found for endpoint [%s]";
    private static final String INVALID_RETURN_TYPE_ERROR_MESSAGE_TEMPLATE = "Handler method [%s] does not conform to expect method signature. Expected = 'public HttpResponse YOUR_METHOD_NAME(QueryParameters queryParameters, String body)'";
    private static final String HANDLER_PARAMETER_NOT_ANNOTATED_ERROR_MESSAGE_TEMPLATE = "All Handler method's parameters must be annotated with one of these three annotations { @RequestHandler, @QueryParameter, @RequestBody }. Affected handler = [%s]";
    private static final String INVALID_HANDLER_METHOD_NO_EMPTY_CONSTRUCTOR_ERROR_MESSAGE_TEMPLATE = "Handler method's classes must have an empty constructor, [%s] does not";
    private static final String INVALID_HANDLER_METHOD_NOT_PUBLIC_ERROR_MESSAGE_TEMPLATE = "Handler method [%s] must be public";

    private List<Class<?>> allowedParameterAnnotations = Arrays.asList(RequestHeader.class, QueryParameter.class, RequestBody.class);

    protected static Map<String, String[]> exposedHeadersByEndpoint = new HashMap<>();

    abstract void setupOperationMap() throws InvalidHandlerMethodException;

    abstract Method getOperation(String endpoint) throws InvalidEndpointException;

    public synchronized HttpResponse handle(HttpRequest httpRequest) throws Exception {
        String endpoint = httpRequest.getPath();

        Method operationHandler = getOperation(endpoint);
        Class<?> operationHandlerClass = operationHandler.getDeclaringClass();
        Object operationHandlerClassInstance = operationHandlerClass.getConstructor().newInstance();

        Object[] parameterArray = getHandlerParameters(httpRequest, operationHandler);
        HttpResponse httpResponse = (HttpResponse) operationHandler.invoke(operationHandlerClassInstance, parameterArray);

        if (exposedHeadersByEndpoint.containsKey(endpoint)) {
            httpResponse.getHeaders().addHeader(Headers.ACCESS_CONTROL_EXPOSE_HEADERS, getExposedHeaders(endpoint));
        }

        return httpResponse;
    }

    private Object[] getHandlerParameters(HttpRequest httpRequest, Method method) {
        Object[] handlerParameters = new Object[method.getParameterCount()];
        Annotation[][] annotationsByParameter = method.getParameterAnnotations();

        for (int i = 0; i < annotationsByParameter.length; i++) {
            for (Annotation annotation : annotationsByParameter[i]) {
                handlerParameters[i] = fillParameter(annotation, httpRequest);
            }
        }

        return handlerParameters;
    }

    private Object fillParameter(Annotation parameterAnnotation, HttpRequest httpRequest) {
        Class<?> annotationType = parameterAnnotation.annotationType();

        Headers headers = httpRequest.getHeaders();
        QueryParameters queryParameters = httpRequest.getQueryParameters();

        if (RequestHeader.class.equals(annotationType)) {
            return headers.getHeader(
                    ((RequestHeader) parameterAnnotation).value()
            );
        } else if (QueryParameter.class.equals(annotationType)) {
            return queryParameters.getParameter(
                    ((QueryParameter) parameterAnnotation).value()
            );
        } else if (RequestBody.class.equals(annotationType)) {
            return httpRequest.getBody();
        }

        return null;
    }

    public Set<Method> getOperationHandlers(Class<? extends Annotation> annotationClass) throws InvalidHandlerMethodException {
        Reflections reflections = new Reflections("", new MethodAnnotationsScanner());
        Set<Method> operationHandlers = reflections.getMethodsAnnotatedWith(annotationClass);
        assertHandlers(operationHandlers);

        return operationHandlers;
    }

    private void assertHandlers(Set<Method> operationsHandlers) throws InvalidHandlerMethodException {
        for (Method handler : operationsHandlers) {
            assertParameters(handler);
            assertReturnType(handler);
            assertEmptyConstructor(handler);
            assertIsPublic(handler);
        }
    }

    private void assertParameters(Method handler) throws InvalidHandlerMethodException {
        if (!(handler.getParameterCount() == 0)) {
            Annotation[][] annotationsByParameters = handler.getParameterAnnotations();
            for (Annotation[] annotationsByParameter : annotationsByParameters) {
                if (annotationsByParameter.length == 0) {
                    throw new InvalidHandlerMethodException(String.format(HANDLER_PARAMETER_NOT_ANNOTATED_ERROR_MESSAGE_TEMPLATE, handler.getName()));
                }

                for (Annotation annotation : annotationsByParameter) {
                    if (!allowedParameterAnnotations.contains(annotation.annotationType())) {
                        throw new InvalidHandlerMethodException(String.format(HANDLER_PARAMETER_NOT_ANNOTATED_ERROR_MESSAGE_TEMPLATE, handler.getName()));
                    }
                }
            }
        }
    }

    private void assertReturnType(Method handler) throws InvalidHandlerMethodException {
        if (handler.getReturnType() != HttpResponse.class) {
            throw new InvalidHandlerMethodException(String.format(INVALID_RETURN_TYPE_ERROR_MESSAGE_TEMPLATE, handler.getReturnType().getSimpleName()));
        }
    }

    private void assertEmptyConstructor(Method handler) throws InvalidHandlerMethodException {
        if (
                Stream
                .of(handler.getClass().getConstructors())
                .anyMatch((constructor) -> constructor.getParameterCount() == 0)
        ) {
            throw new InvalidHandlerMethodException(String.format(INVALID_HANDLER_METHOD_NO_EMPTY_CONSTRUCTOR_ERROR_MESSAGE_TEMPLATE, handler.getDeclaringClass().getSimpleName()));
        }
    }

    private void assertIsPublic(Method handler) throws InvalidHandlerMethodException {
        if (handler.isAccessible()) {
            throw new InvalidHandlerMethodException(String.format(INVALID_HANDLER_METHOD_NOT_PUBLIC_ERROR_MESSAGE_TEMPLATE, handler.getName()));
        }
    }

    protected String getExposedHeaders(String path) {
        return String.join(SEPARATOR, exposedHeadersByEndpoint.get(path));
    }

    protected void throwInvalidEndpointException(String endpoint) throws InvalidEndpointException {
        throw new InvalidEndpointException(String.format(INVALID_ENDPOINT_ERROR_MESSAGE_TEMPLATE, endpoint));
    }
}

package controller;

import exceptions.InvalidHandlerMethodException;
import model.HttpRequest;
import model.HttpResponse;
import model.QueryParameters;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;

public abstract class HttpMethodController {
    private static final String INVALID_RETURN_TYPE_ERROR_MESSAGE_TEMPLATE = "Handler method [%s] does not conform to expect method signature. Expected = 'public HttpResponse YOUR_METHOD_NAME(QueryParameters queryParameters, String body)'";
    private static final String INVALID_HANDLER_METHOD_SIGNATURE_ERROR_MESSAGE_TEMPLATE = "Handler methods must return an object of type String. Actual = [%s]";
    private static final String INVALID_HANDLER_METHOD_NO_EMPTY_CONSTRUCTOR_ERROR_MESSAGE_TEMPLATE = "Handler method's classes must have an empty constructor, [%s] does not";
    private static final String INVALID_HANDLER_METHOD_NOT_PUBLIC_ERROR_MESSAGE_TEMPLATE = "Handler method [%s] must be public";

    public abstract HttpResponse handle(HttpRequest httpRequest) throws Exception;

    abstract void setupOperationMap() throws InvalidHandlerMethodException;

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
        if (handler.getParameterCount() != 2) {
            throw new InvalidHandlerMethodException(String.format(INVALID_HANDLER_METHOD_SIGNATURE_ERROR_MESSAGE_TEMPLATE, handler.getName()));
        }

        Class<?>[] parameterTypes = handler.getParameterTypes();
        if (parameterTypes[0] != QueryParameters.class || parameterTypes[1] != String.class) {
            throw new InvalidHandlerMethodException(String.format(INVALID_HANDLER_METHOD_SIGNATURE_ERROR_MESSAGE_TEMPLATE, handler.getName()));
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
}

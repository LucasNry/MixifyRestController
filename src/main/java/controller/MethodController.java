package controller;

import exceptions.InvalidEndpointException;
import exceptions.InvalidHandlerMethodException;
import model.Protocol;
import model.ProtocolRequest;
import model.ProtocolResponse;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public abstract class MethodController {
    protected static final String SEPARATOR = ", ";

    private static final String INVALID_ENDPOINT_ERROR_MESSAGE_TEMPLATE = "No handler method was found for endpoint [%s]";
    private static final String INVALID_RETURN_TYPE_ERROR_MESSAGE_TEMPLATE = "Handler method [%s] does not conform to expect method signature. It should return a response message from the appropriate communication protocol";
    private static final String INVALID_HANDLER_METHOD_NO_EMPTY_CONSTRUCTOR_ERROR_MESSAGE_TEMPLATE = "Handler method's classes must have an empty constructor, [%s] does not";
    private static final String INVALID_HANDLER_METHOD_NOT_PUBLIC_ERROR_MESSAGE_TEMPLATE = "Handler method [%s] must be public";
    public static final String HANDLER_RECEIVING_MORE_THAN_ONE_PARAMETER_ERROR_MESSAGE = "Handler methods must receive a single argument and it must be a request message from the appropriate communication protocol";
    public static final String INVALID_INPUT_PARAMETER_TYPE_ERROR_MESSAGE_TEMPLATE = "Handler method input parameter should receive an instance of a request message from the appropriate protocol. [%s] does not extend CommunicationProtocol";

    protected Map<String, Method> operationsMap;

    private Protocol protocol;

    public MethodController(Protocol protocol) {
        this.protocol = protocol;
    }

    abstract void setupOperationMap() throws InvalidHandlerMethodException;

    private Method getOperation(String endpoint) throws InvalidEndpointException {
        if (!operationsMap.containsKey(endpoint)) {
            throwInvalidEndpointException(endpoint);
        }

        return operationsMap.get(endpoint);
    }

    public synchronized ProtocolResponse handle(ProtocolRequest request) throws Exception {
        String endpoint = request.getPath();

        Method operationHandler = getOperation(endpoint);
        Class<?> operationHandlerClass = operationHandler.getDeclaringClass();
        Object operationHandlerClassInstance = operationHandlerClass.getConstructor().newInstance();

        return (ProtocolResponse) operationHandler.invoke(
                operationHandlerClassInstance,
                operationHandler.getParameterCount() > 0 ? new Object[]{request} : new Object[0]
        );
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
            try {
                Assert.isTrue(handler.getParameterCount() == 1, HANDLER_RECEIVING_MORE_THAN_ONE_PARAMETER_ERROR_MESSAGE);
                Assert.isInstanceOf(protocol.requestClass, handler.getParameterTypes()[0], String.format(INVALID_INPUT_PARAMETER_TYPE_ERROR_MESSAGE_TEMPLATE, handler.getParameterTypes()[0].getSimpleName()));
            } catch (IllegalArgumentException e) {
                throw new InvalidHandlerMethodException(e);
            }
        }
    }

    private void assertReturnType(Method handler) throws InvalidHandlerMethodException {
        if (handler.getReturnType() != protocol.responseClass) {
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

    protected void throwInvalidEndpointException(String endpoint) throws InvalidEndpointException {
        throw new InvalidEndpointException(String.format(INVALID_ENDPOINT_ERROR_MESSAGE_TEMPLATE, endpoint));
    }
}

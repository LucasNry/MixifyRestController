package controller;

import annotations.GetOperation;
import exceptions.InvalidEndpointException;
import exceptions.InvalidHandlerMethodException;
import model.HttpRequest;
import model.HttpResponse;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GetController extends HttpMethodController {
    private static final String INVALID_ENDPOINT_ERROR_MESSAGE_TEMPLATE = "No handler method was found for endpoint [%s]";

    private Map<String, Method> operationsMap;

    public GetController() throws InvalidHandlerMethodException {
        setupOperationMap();
    }

    @Override
    void setupOperationMap() throws InvalidHandlerMethodException {
        Set<Method> operationHandlers = getOperationHandlers(GetOperation.class);
        operationsMap = operationHandlers
                .stream()
                .collect(
                        Collectors.toMap(
                                method -> method.getAnnotation(GetOperation.class).endpoint(),
                                Function.identity()
                        )
                );
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws Exception {
        String endpoint = httpRequest.getPath();

        if (!operationsMap.containsKey(endpoint)) {
            throw new InvalidEndpointException(String.format(INVALID_ENDPOINT_ERROR_MESSAGE_TEMPLATE, endpoint));
        }

        Method operationHandler = operationsMap.get(endpoint);

        Class<?> operationHandlerClass = operationHandler.getDeclaringClass();
        Object operationHandlerClassInstance = operationHandlerClass.getConstructor().newInstance();

        return (HttpResponse) operationHandler
                .invoke(operationHandlerClassInstance, httpRequest.getQueryParameters(), httpRequest.getBody()); // Can't call method without instance
    }
}

package controller;

import annotations.ExposeHeaders;
import annotations.PostOperation;
import exceptions.InvalidEndpointException;
import exceptions.InvalidHandlerMethodException;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class PostController extends HttpMethodController {

    private Map<String, Method> operationsMap;

    @Override
    void setupOperationMap() throws InvalidHandlerMethodException {
        Set<Method> operationHandlers = getOperationHandlers(PostOperation.class);
        operationsMap = new HashMap<>();
        for (Method operationHandler : operationHandlers) {
            String endpoint = operationHandler.getAnnotation(PostOperation.class).endpoint();

            operationsMap.put(endpoint, operationHandler);
            if (operationHandler.isAnnotationPresent(ExposeHeaders.class)) {
                String[] exposeHeaders = operationHandler.getAnnotation(ExposeHeaders.class).keys();

                exposedHeadersByEndpoint.put(endpoint, exposeHeaders);
            }
        }
    }

    @Override
    Method getOperation(String endpoint) throws InvalidEndpointException {
        if (!operationsMap.containsKey(endpoint)) {
            throwInvalidEndpointException(endpoint);
        }

        return operationsMap.get(endpoint);
    }
}

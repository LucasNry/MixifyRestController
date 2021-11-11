package controller;

import annotations.ExposeHeaders;
import annotations.GetOperation;
import exceptions.InvalidEndpointException;
import exceptions.InvalidHandlerMethodException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class GetController extends HttpMethodController {
    private Map<String, Method> operationsMap;

    public GetController() throws InvalidHandlerMethodException {
        setupOperationMap();
    }

    @Override
    void setupOperationMap() throws InvalidHandlerMethodException {
        Set<Method> operationHandlers = getOperationHandlers(GetOperation.class);
        operationsMap = new HashMap<>();
        for (Method operationHandler : operationHandlers) {
            String endpoint = operationHandler.getAnnotation(GetOperation.class).endpoint();

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

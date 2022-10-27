package controller;

import annotations.PostOperation;
import exceptions.InvalidHandlerMethodException;
import model.Protocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PostController extends MethodController {

    private Map<String, Method> operationsMap;

    public PostController(Protocol protocol) {
        super(protocol);
    }

    @Override
    void setupOperationMap() throws InvalidHandlerMethodException {
        Set<Method> operationHandlers = getOperationHandlers(PostOperation.class);
        operationsMap = new HashMap<>();
        for (Method operationHandler : operationHandlers) {
            String endpoint = operationHandler.getAnnotation(PostOperation.class).endpoint();

            operationsMap.put(endpoint, operationHandler);
        }
    }
}

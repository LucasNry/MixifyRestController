package controller;

import annotations.GetOperation;
import exceptions.InvalidHandlerMethodException;
import model.Protocol;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

@Component
public class GetController extends MethodController {
//    private Map<String, Method> operationsMap;

    public GetController(Protocol protocol) throws InvalidHandlerMethodException {
        super(protocol);
        setupOperationMap();
    }

    @Override
    void setupOperationMap() throws InvalidHandlerMethodException {
        Set<Method> operationHandlers = getOperationHandlers(GetOperation.class);
        operationsMap = new HashMap<>();
        for (Method operationHandler : operationHandlers) {
            String endpoint = operationHandler.getAnnotation(GetOperation.class).endpoint();

            operationsMap.put(endpoint, operationHandler);
        }
    }
}

package controller;

import annotations.GetOperation;
import exceptions.InvalidEndpointException;
import exceptions.InvalidHandlerMethodException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GetController extends HttpMethodController {
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
    Method getOperation(String endpoint) throws InvalidEndpointException {
        if (!operationsMap.containsKey(endpoint)) {
            throwInvalidEndpointException(endpoint);
        }

        return operationsMap.get(endpoint);
    }
}

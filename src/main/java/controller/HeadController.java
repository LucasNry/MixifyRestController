package controller;

import annotations.GetOperation;
import exceptions.InvalidHandlerMethodException;
import lombok.NoArgsConstructor;
import model.HttpRequest;
import model.HttpResponse;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
public class HeadController extends HttpMethodController {

    private Map<String, Method> operationsMap;

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws Exception {
        return null;
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
}

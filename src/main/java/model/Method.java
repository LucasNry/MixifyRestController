package model;

import controller.GetController;
import controller.MethodController;
import controller.PostController;

public enum Method {
    GET(GetController.class),
    POST(PostController.class);

    private static final String VERSION_NOT_FOUND_ERROR_TEMPLATE = "No Method was found for String [%s]";

    private Class<? extends MethodController> controller;

    private Object singletonInstance;

    Method(Class<? extends MethodController> controllerClazz) {
        this.controller = controllerClazz;
    }

    public static Method fromString(String methodString) {
        for (Method method : Method.values()) {
            if (method.toString().equals(methodString)) {
                return method;
            }
        }

        throw new IllegalArgumentException(String.format(VERSION_NOT_FOUND_ERROR_TEMPLATE, methodString));
    }

    public <T extends MethodController> T getController(Protocol protocol) throws Exception {
        if (singletonInstance == null) {
            singletonInstance = controller.getDeclaredConstructor(Protocol.class).newInstance(protocol);
        }

        return (T) singletonInstance;
    }
}

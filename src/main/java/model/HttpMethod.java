package model;

import controller.DeleteController;
import controller.GetController;
import controller.HeadController;
import controller.HttpMethodController;
import controller.OptionsController;
import controller.PostController;
import controller.PutController;

public enum HttpMethod {
    GET(GetController.class),
    HEAD(HeadController.class),
    POST(PostController.class),
    PUT(PutController.class),
    DELETE(DeleteController.class),
    OPTIONS(OptionsController.class);

    private static final String VERSION_NOT_FOUND_ERROR_TEMPLATE = "No Method was found for String [%s]";

    private Class<? extends HttpMethodController> controller;

    private Object singletonInstance;

    HttpMethod(Class<? extends HttpMethodController> controllerClazz) {
        this.controller = controllerClazz;
    }

    public static HttpMethod fromString(String methodString) {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.toString().equals(methodString)) {
                return httpMethod;
            }
        }

        throw new IllegalArgumentException(String.format(VERSION_NOT_FOUND_ERROR_TEMPLATE, methodString));
    }

    public <T extends HttpMethodController> T getController() throws IllegalAccessException, InstantiationException {
        if (singletonInstance == null) {
            singletonInstance = controller.newInstance();
        }

        return (T) singletonInstance;
    }
}

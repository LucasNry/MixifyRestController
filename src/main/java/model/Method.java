package model;

import controller.DeleteController;
import controller.GetController;
import controller.HeadController;
import controller.HttpMethodController;
import controller.PostController;
import controller.PutController;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Method {
    GET(GetController.class),
    HEAD(HeadController.class),
    POST(PostController.class),
    PUT(PutController.class),
    DELETE(DeleteController.class);

    private static final String VERSION_NOT_FOUND_ERROR_TEMPLATE = "No Method was found for String [%s]";

    private Class<? extends HttpMethodController> controller;

    public static Method fromString(String methodString) {
        for (Method method : Method.values()) {
            if (method.toString().equals(methodString)) {
                return method;
            }
        }

        throw new IllegalArgumentException(String.format(VERSION_NOT_FOUND_ERROR_TEMPLATE, methodString));
    }

    public <T extends HttpMethodController> T getController() throws IllegalAccessException, InstantiationException {
        return (T) controller.newInstance();
    }
}

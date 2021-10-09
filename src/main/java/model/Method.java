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

    private Class<? extends HttpMethodController> controller;

    public <T extends HttpMethodController> T getController() throws IllegalAccessException, InstantiationException {
        return (T) controller.newInstance();
    }
}

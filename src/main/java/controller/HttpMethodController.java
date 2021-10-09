package controller;

import model.HttpRequest;
import model.HttpResponse;

public interface HttpMethodController {
    HttpResponse handle(HttpRequest httpRequest);
}

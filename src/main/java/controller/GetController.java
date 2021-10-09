package controller;

import lombok.NoArgsConstructor;
import model.HttpRequest;
import model.HttpResponse;

@NoArgsConstructor
public class GetController implements HttpMethodController {
    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        return null;
    }
}

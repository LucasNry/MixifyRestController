package handler;

import controller.HttpMethodController;
import model.HttpRequest;
import model.HttpResponse;
import model.Method;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class RequestHandler {

    public void handleRequest(InputStream clientInputStream, OutputStream clientOutputStream) throws Exception {
        HttpRequest httpRequest = getHttpRequest(clientInputStream);
        Method requestMethod = httpRequest.getMethod();

        HttpMethodController httpMethodController = requestMethod.getController();
        HttpResponse httpResponse = httpMethodController.handle(httpRequest);

        String stringifiedResponse = httpResponse.toString();
        clientOutputStream.write(stringifiedResponse.getBytes());
    }

    private HttpRequest getHttpRequest(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] requestBuffer = new byte[dataInputStream.available()];
        dataInputStream.readFully(requestBuffer);

        String requestAsString = new String(requestBuffer);
        return HttpRequest.fromString(requestAsString);
    }
}

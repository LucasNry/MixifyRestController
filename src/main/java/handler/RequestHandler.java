package handler;

import controller.HttpMethodController;
import model.HttpRequest;
import model.HttpResponse;
import model.Method;
import model.RequestStatus;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private Socket clientSocket;
    private InputStream clientInputStream;
    private OutputStream clientOutputStream;

    public RequestHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientInputStream = clientSocket.getInputStream();
        this.clientOutputStream = clientSocket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            handleRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRequest() throws Exception {
        HttpRequest httpRequest;

        try {
            httpRequest = getHttpRequest(clientInputStream);
        } catch (Exception e) {
            postHttpResponse(
                    HttpResponse
                        .builder()
                        .requestStatus(RequestStatus.BAD_REQUEST)
                        .build()
            );
            return;
        }

        Method requestMethod = httpRequest.getMethod();

        HttpMethodController httpMethodController = requestMethod.getController();
        HttpResponse httpResponse;
        try {
            httpResponse = httpMethodController.handle(httpRequest);
        } catch (Exception e) {
            httpResponse = HttpResponse
                    .builder()
                    .requestStatus(RequestStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }

        postHttpResponse(httpResponse);

        clientInputStream.close();
        clientOutputStream.close();
        clientSocket.close();
    }

    private HttpRequest getHttpRequest(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] requestBuffer = new byte[dataInputStream.available()];
        dataInputStream.readFully(requestBuffer);

        String requestAsString = new String(requestBuffer);
        return HttpRequest.fromString(requestAsString);
    }

    private void postHttpResponse(HttpResponse httpResponse) throws IOException {
        String stringifiedResponse = httpResponse.toString();
        clientOutputStream.write(stringifiedResponse.getBytes());
    }
}

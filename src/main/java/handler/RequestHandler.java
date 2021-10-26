package handler;

import controller.HttpMethodController;
import model.ConnectionType;
import model.Headers;
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

    private static final String KEEP_ALIVE_SEPARATOR = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";

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
            handle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle() throws Exception {
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

        Headers requestHeaders = httpRequest.getHeaders();
        String connectionHeader = requestHeaders.getHeader(Headers.CONNECTION);
        ConnectionType connectionType = ConnectionType.fromValue(connectionHeader);

        switch (connectionType) {
            case KEEP_ALIVE:
                handleRequest(httpRequest);
                handleKeepAliveRequest(httpRequest);
                break;
            case CLOSE:
            default:
                handleRequest(httpRequest);
                break;
        }

        clientInputStream.close();
        clientOutputStream.close();
        clientSocket.close();
    }

    private void handleKeepAliveRequest(HttpRequest httpRequest) throws Exception {
        Headers headers = httpRequest.getHeaders();
        String keepAliveHeaderContent = headers.getHeader(Headers.KEEP_ALIVE);
        String[] splitContent = keepAliveHeaderContent.split(KEEP_ALIVE_SEPARATOR);

        int timeout = getValueFromKVPair(splitContent[0]); // ms
        int maxCon = getValueFromKVPair(splitContent[1].substring(1)); // Removing preceding space

        int nOfConnections = 0;
        long timeOfLastRequest = System.currentTimeMillis();
        boolean isKeepAlive;
        while (
                nOfConnections <= maxCon &&
                (System.currentTimeMillis() - timeOfLastRequest) <= timeout
        ) {
            if (clientInputStream.available() > 0) {
                HttpRequest newHttpRequest;
                try {
                    newHttpRequest = getHttpRequest(clientInputStream);
                } catch (Exception e) {
                    postHttpResponse(
                            HttpResponse
                                    .builder()
                                    .requestStatus(RequestStatus.BAD_REQUEST)
                                    .build()
                    );
                    return;
                }

                timeOfLastRequest = System.currentTimeMillis();
                handleRequest(newHttpRequest);

                isKeepAlive = isKeepAlive(newHttpRequest.getHeaders());
                if (!isKeepAlive) {
                    return;
                }
            }

            nOfConnections++;
        }

        postHttpResponse(
                HttpResponse
                        .builder()
                        .requestStatus(RequestStatus.REQUEST_TIMEOUT)
                        .build()
        );
    }

    private void handleRequest(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = handleHttpRequest(httpRequest);
        postHttpResponse(httpResponse);
    }

    private HttpResponse handleHttpRequest(HttpRequest httpRequest) throws Exception {
        Method requestMethod = httpRequest.getMethod();
        HttpMethodController httpMethodController = requestMethod.getController();

        try {
            return httpMethodController.handle(httpRequest);
        } catch (Exception e) {
            return HttpResponse
                    .builder()
                    .requestStatus(RequestStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private boolean isKeepAlive(Headers headers) {
        String keepAliveHeaderValue = headers.getHeader(Headers.KEEP_ALIVE);

        return ConnectionType.fromValue(keepAliveHeaderValue).equals(ConnectionType.KEEP_ALIVE);
    }

    private int getValueFromKVPair(String timeoutKeyValuePair) {
        return Integer.parseInt(timeoutKeyValuePair.split(KEY_VALUE_SEPARATOR)[1]);
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

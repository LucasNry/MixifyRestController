package handler;

import controller.HttpMethodController;
import lombok.SneakyThrows;
import model.ConnectionType;
import model.Headers;
import model.HttpRequest;
import model.HttpResponse;
import model.HttpMethod;
import model.RequestStatus;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class RequestHandler implements Runnable {

    private static final String KEEP_ALIVE_SEPARATOR = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int DEFAULT_MAX_CON = 1;

    private Socket clientSocket;
    private InputStream clientInputStream;
    private OutputStream clientOutputStream;

    public RequestHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientInputStream = clientSocket.getInputStream();
        this.clientOutputStream = clientSocket.getOutputStream();
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            handle();
        } catch (Exception e) {
            e.printStackTrace();
            postHttpResponse(
                    HttpResponse
                            .builder()
                            .requestStatus(RequestStatus.INTERNAL_SERVER_ERROR)
                            .build()
            );
        } finally {
            closeStreams();
        }
    }

    public void handle() throws Exception {
        HttpRequest httpRequest;

        try {
            httpRequest = getHttpRequest();
        } catch (Exception e) {
            e.printStackTrace();
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
                handleKeepAliveRequest(httpRequest);
                break;
            case CLOSE:
            default:
                handleRequest(httpRequest);
                break;
        }
    }

    private void handleKeepAliveRequest(HttpRequest httpRequest) throws Exception {
        handleRequest(httpRequest);

        Headers headers = httpRequest.getHeaders();
        int timeout;
        int maxCon;

        String keepAliveHeaderContent = headers.getHeader(Headers.KEEP_ALIVE);
        if (keepAliveHeaderContent != null) {
            String[] splitContent = keepAliveHeaderContent.split(KEEP_ALIVE_SEPARATOR);

            timeout = getValueFromKVPair(splitContent[0]); // ms
            maxCon = getValueFromKVPair(splitContent[1].substring(1)); // Removing preceding space
        } else {
            timeout = DEFAULT_TIMEOUT;
            maxCon = DEFAULT_MAX_CON;
        }

        int nOfConnections = 0;
        long timeOfLastRequest = System.currentTimeMillis();
        boolean isKeepAlive;

        do {
            if (clientInputStream.available() <= 0) {
                continue;
            }

            HttpRequest newHttpRequest;
            try {
                newHttpRequest = getHttpRequest();
            } catch (Exception e) {
                e.printStackTrace();
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

            nOfConnections++;
        } while (
                nOfConnections <= maxCon &&
                (System.currentTimeMillis() - timeOfLastRequest) <= timeout
        );
    }

    private void handleRequest(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = handleHttpRequest(httpRequest);
        postHttpResponse(httpResponse);
    }

    private HttpResponse handleHttpRequest(HttpRequest httpRequest) throws Exception {
        HttpMethod requestHttpMethod = httpRequest.getHttpMethod();
        HttpMethodController httpMethodController = requestHttpMethod.getController();

        try {
            return httpMethodController.handle(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResponse
                    .builder()
                    .requestStatus(RequestStatus.INTERNAL_SERVER_ERROR)
                    .body(Arrays.toString(e.getStackTrace()))
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

    private HttpRequest getHttpRequest() throws IOException {
        byte[] requestBuffer = readInput();
        String requestAsString = new String(requestBuffer);

        if (requestAsString.isEmpty()) {
            throw new IllegalArgumentException("Request can't be empty");
        }

        return HttpRequest.fromString(requestAsString);
    }

    private void postHttpResponse(HttpResponse httpResponse) throws IOException {
        String stringifiedResponse = httpResponse.toString();
        clientOutputStream.write(stringifiedResponse.getBytes());
    }

    private byte[] readInput() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(clientInputStream);

        while(dataInputStream.available() <= 0);

        byte[] requestBuffer = new byte[dataInputStream.available()];
        dataInputStream.readFully(requestBuffer);

        return requestBuffer;
    }

    private void closeStreams() throws IOException {
        clientInputStream.close();
        clientOutputStream.close();
        clientSocket.close();
    }
}

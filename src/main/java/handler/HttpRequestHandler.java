package handler;

import model.ConnectionType;
import model.Headers;
import model.HttpRequest;
import model.HttpResponse;
import model.Protocol;
import model.RequestStatus;

import java.io.IOException;
import java.net.Socket;

public class HttpRequestHandler extends AbstractRequestHandler<HttpRequest, HttpResponse> {

    private static final String KEEP_ALIVE_SEPARATOR = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int DEFAULT_MAX_CON = 1;

    public HttpRequestHandler(Socket clientSocket, Protocol protocol) throws IOException {
        super(clientSocket, protocol);
    }

    @Override
    public void handle() throws Exception {
        HttpRequest httpRequest;

        try {
            httpRequest = parseRequest();
        } catch (Exception e) {
            e.printStackTrace();
            postResponse(createParsingErrorResponse());
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
                super.handle();
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
                newHttpRequest = parseRequest();
            } catch (Exception e) {
                e.printStackTrace();
                postResponse(
                        createParsingErrorResponse()
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

    protected HttpRequest parseRequest() throws IOException {
        byte[] requestBuffer = readInput();
        String requestAsString = new String(requestBuffer);

        if (requestAsString.isEmpty()) {
            throw new IllegalArgumentException("Request can't be empty");
        }

        return HttpRequest.fromString(requestAsString);
    }

    private boolean isKeepAlive(Headers headers) {
        String keepAliveHeaderValue = headers.getHeader(Headers.KEEP_ALIVE);

        return ConnectionType.fromValue(keepAliveHeaderValue).equals(ConnectionType.KEEP_ALIVE);
    }

    private int getValueFromKVPair(String timeoutKeyValuePair) {
        return Integer.parseInt(timeoutKeyValuePair.split(KEY_VALUE_SEPARATOR)[1]);
    }

    protected void postResponse(HttpResponse response) throws IOException {
        String stringifiedResponse = response.toString();
        clientOutputStream.write(stringifiedResponse.getBytes());
    }

    protected HttpResponse createParsingErrorResponse() {
        return HttpResponse
                .builder()
                .requestStatus(RequestStatus.BAD_REQUEST)
                .build();
    }

    protected HttpResponse createInternalErrorResponse() {
        return HttpResponse
                .builder()
                .requestStatus(RequestStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}

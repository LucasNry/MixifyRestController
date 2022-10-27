package handler;

import controller.MethodController;
import lombok.SneakyThrows;
import model.Protocol;
import model.ProtocolRequest;
import model.Method;
import model.ProtocolResponse;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class AbstractRequestHandler<I extends ProtocolRequest, O extends ProtocolResponse> implements Runnable {

    private Socket clientSocket;
    private Protocol protocol;
    protected InputStream clientInputStream;
    protected OutputStream clientOutputStream;

    public AbstractRequestHandler(Socket clientSocket, Protocol protocol) throws IOException {
        this.clientSocket = clientSocket;
        this.clientInputStream = clientSocket.getInputStream();
        this.clientOutputStream = clientSocket.getOutputStream();
        this.protocol = protocol;
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            handle();
        } catch (Exception e) {
            e.printStackTrace();
            postResponse(createInternalErrorResponse());
        } finally {
            closeStreams();
        }
    }

    protected abstract O createInternalErrorResponse();

    public void handle() throws Exception {
        I request;

        try {
            request = parseRequest();
        } catch (Exception e) {
            e.printStackTrace();
            postResponse(createParsingErrorResponse());
            return;
        }

        handleRequest(request);
    }

    protected abstract O createParsingErrorResponse();

    protected void handleRequest(I request) throws Exception {
        O response = getResponseFromController(request);
        postResponse(response);
    }

    private O getResponseFromController(I request) throws Exception {
        Method requestMethod = request.getMethod();
        MethodController methodController = requestMethod.getController(protocol);

        try {
            return (O) methodController.handle(request);
        } catch (Exception e) {
            e.printStackTrace();
            return createInternalErrorResponse();
        }
    }

    protected abstract I parseRequest() throws IOException;

    protected abstract void postResponse(O response) throws IOException;

    protected byte[] readInput() throws IOException {
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

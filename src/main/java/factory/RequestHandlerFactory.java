package factory;

import handler.AbstractRequestHandler;
import handler.HttpRequestHandler;
import model.Protocol;

import java.io.IOException;
import java.net.Socket;

public class RequestHandlerFactory {

    public static AbstractRequestHandler createHandlerFactory(Protocol protocol, Socket socket) throws IOException {
        switch (protocol) {
            case HTTP:
                return new HttpRequestHandler(socket, protocol);
            default:
                throw new IllegalArgumentException(String.format("No handler exist for protocol [%s]", protocol.name()));
        }
    }
}

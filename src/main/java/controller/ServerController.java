package controller;

import factory.RequestHandlerFactory;
import model.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class ServerController {

    private ServerSocket serverSocket;

    private Protocol protocol;

    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    public ServerController(int port, Protocol protocol) throws IOException {
        serverSocket = new ServerSocket(port);
        this.protocol = protocol;
    }

    public void start() throws Exception {
        System.out.println(String.format("Running on port [%s]", serverSocket.getLocalPort()));

        while (true) {
            Socket socket = serverSocket.accept();
            threadPoolExecutor.submit(RequestHandlerFactory.createHandlerFactory(protocol, socket));
        }
    }
}

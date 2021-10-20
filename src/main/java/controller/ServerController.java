package controller;

import handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class ServerController {

    private ServerSocket serverSocket;

    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    public ServerController(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println(String.format("Running on port [%s]", port));
    }

    public void start() throws Exception {
        while (true) {
            Socket socket = serverSocket.accept();
            threadPoolExecutor.submit(new RequestHandler(socket));
        }
    }
}

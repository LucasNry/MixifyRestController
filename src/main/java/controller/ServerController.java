package controller;

import handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class ServerController {

    private ServerSocket serverSocket;

    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public ServerController(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() throws Exception {
        RequestHandler requestHandler = new RequestHandler(); // Consider using singleton Spring bean

        while (true) {
            Socket socket = serverSocket.accept();
            threadPoolExecutor.submit(() -> {
                try (Socket clientSocket = socket) {
                    requestHandler.handleRequest(clientSocket.getInputStream(), clientSocket.getOutputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

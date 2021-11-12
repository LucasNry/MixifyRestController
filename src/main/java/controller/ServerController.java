package controller;

import handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ServerController {

    private ServerSocket serverSocket;

    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private ScheduledExecutorService canceller = Executors.newSingleThreadScheduledExecutor();

    public ServerController(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println(String.format("Running on port [%s]", port));
    }

    public void start() throws Exception {
        while (true) {
            Socket socket = serverSocket.accept();
            if (threadPoolExecutor.getQueue().size() > 0) System.out.println(threadPoolExecutor.getQueue().size());
            executeTask(socket, 10000);
        }
    }


    public void executeTask(Socket clientSocket, long timeoutMS) throws IOException {
        final Future<?> future = threadPoolExecutor.submit(new RequestHandler(clientSocket));
        canceller.schedule(() -> {
            boolean result = future.cancel(true);
            System.out.println(String.format("result = %s; isCancelled = %s", result, future.isCancelled()));
        }, timeoutMS, TimeUnit.MILLISECONDS);
    }
}

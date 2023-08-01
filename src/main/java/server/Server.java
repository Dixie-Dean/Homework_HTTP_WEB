package server;

import handler.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private static ConcurrentHashMap<String, Handler> handlers;
    private final Executor threadPool;

    public Server() {
        threadPool = Executors.newFixedThreadPool(64);
        handlers = new ConcurrentHashMap<>();
    }

    protected static ConcurrentHashMap<String, Handler> getHandlers() {
        return handlers;
    }

    public void launch(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running...");
            while (!serverSocket.isClosed()) {
                Connection connection = new Connection(serverSocket.accept());
                threadPool.execute(connection);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        String key = method + "=" + path;
        handlers.put(key, handler);
    }
}
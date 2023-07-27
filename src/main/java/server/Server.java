package server;

import handler.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
//    private static final List<String> validPaths = List.of("/index.html",
//            "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js",
//            "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    private static ConcurrentHashMap<HashMap<String, String>, Handler> handlers;
    private final Executor threadPool;

    public Server() {
        threadPool = Executors.newFixedThreadPool(64);
        handlers = new ConcurrentHashMap<>();
    }

    protected static ConcurrentHashMap<HashMap<String, String>, Handler> getHandlers() {
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
        HashMap<String, String> auxiliaryMap = new HashMap<>();
        auxiliaryMap.put(method, path);
        handlers.put(auxiliaryMap, handler);
    }
}

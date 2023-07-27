package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.*;

public class Server {
    private static final List<String> validPaths = List.of("/index.html",
            "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js",
            "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final Executor threadPool;

    public Server() {
        threadPool = Executors.newFixedThreadPool(64);
    }

    public void launch(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running...");
            while (!serverSocket.isClosed()) {
                RequestHandler handler = new RequestHandler(serverSocket.accept());
                threadPool.execute(handler);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected static List<String> getValidPaths() {
        return validPaths;
    }
}

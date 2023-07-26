package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

public class Server {
    private static final List<String> validPaths = List.of("/index.html",
            "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js",
            "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public void launch() {
        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            System.out.println("Server is running...");
            while (!serverSocket.isClosed()) {
                RequestHandler handler = new RequestHandler(serverSocket.accept());
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    protected static List<String> getValidPaths() {
        return validPaths;
    }
}

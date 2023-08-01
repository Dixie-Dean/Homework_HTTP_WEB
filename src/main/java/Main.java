import handler.Handler;
import server.Server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        Handler defaultHandler = (request, responseStream) -> {
            final var filePath = Path.of(".", "public", request.getPath());
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, responseStream);
            responseStream.flush();
        };

        server.addHandler("GET", "/app.js", defaultHandler);
        server.addHandler("GET", "/events.html", defaultHandler);
        server.addHandler("GET", "/events.js", defaultHandler);
        server.addHandler("GET", "/index.html", defaultHandler);
        server.addHandler("GET", "/links.html", defaultHandler);
        server.addHandler("GET", "/resources.html", defaultHandler);
        server.addHandler("GET", "/spring.png", defaultHandler);
        server.addHandler("GET", "/spring.svg", defaultHandler);
        server.addHandler("GET", "/styles.css", defaultHandler);

        server.addHandler("GET", "/classic.html", (request, responseStream) -> {
            final var filePath = Path.of(".", "public", request.getPath());
            final var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);
            final var content = template.replace("{time}", LocalDateTime.now().toString()).getBytes();
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.write(content);
            responseStream.flush();
        });

        server.addHandler("GET", "/default-get.html", ((request, responseStream) -> {
            final var filePath = Path.of(".", "static", request.getPath());
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            responseStream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
            ).getBytes());
            Files.copy(filePath, responseStream);
            responseStream.flush();
        }));

        server.addHandler("POST", "/default-get.html", ((request, responseStream) -> {
            final var filePath = Path.of(".", "static", request.getPath());
            final var mimeType = Files.probeContentType(filePath);
            final var length = Files.size(filePath);
            responseStream.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    request.getBody()
            ).getBytes());
            Files.copy(filePath, responseStream);
            responseStream.flush();
        }));

//        server.addHandler("GET", "/default-get.html", ((request, responseStream) -> {
//            final var filePath = Path.of(".", "static", request.getPath());
//            final var mimeType = Files.probeContentType(filePath);
//            final var length = Files.size(filePath);
//            responseStream.write(("HTTP/1.1 200 OK\r\n" +
//                    "Content-Type: " + mimeType + "\r\n" +
//                    "Content-Length: " + length + "\r\n" +
//                    "Connection: close\r\n" +
//                    "\r\n" +
//                    request.getBody()
//            ).getBytes());
//            Files.copy(filePath, responseStream);
//            responseStream.flush();
//        }));

        server.launch(9999);
    }
}
package server;

import handler.Handler;
import request.Request;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Connection implements Runnable {
    private final Socket socket;
    private final BufferedReader inputStream;
    private final BufferedOutputStream outputStream;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            handleRequest();
        } catch (IOException e) {
            disconnect(socket, inputStream, outputStream);
        }
    }

    private void handleRequest() throws IOException {
        final var requestLine = inputStream.readLine();
        System.out.println(requestLine); //todo DELETE THIS LINE
        final var parts = requestLine.split(" ");
        checkRequestLineLength(parts);

        final var request = new Request(parts[0], parts[1], parts[2]);
        Handler handler = Server.getHandlers().get(request.getKey());
        handler.handle(request, outputStream);


//        final var filePath = Path.of(".", "public", path);
//        final var mimeType = Files.probeContentType(filePath);
//        if (path.equals("/classic.html")) {
//            responseForClassic(filePath, mimeType);
//        } else {
//            response(filePath, mimeType);
//        }
    }

    private void checkRequestLineLength(String[] parts) throws IOException {
        if (parts.length != 3) {
            outputStream.write((
                    """
                            HTTP/1.1 400 Bad Request\r
                            Content-Length: 0\r
                            Connection: close\r
                            \r
                            """
                    ).getBytes());
            outputStream.flush();
            disconnect(socket, inputStream, outputStream);
        }
    }

//    private void checkPathValidity(String path) throws IOException {
//        if (!Server.getValidPaths().contains(path)) {
//            outputStream.write((
//                    """
//                            HTTP/1.1 404 Not Found\r
//                            Content-Length: 0\r
//                            Connection: close\r
//                            \r
//                            """
//            ).getBytes());
//            outputStream.flush();
//            disconnect(socket, inputStream, outputStream);
//        }
//    }

//    private void responseForClassic(Path filePath, String mimeType) throws IOException {
//        final var template = Files.readString(filePath);
//        final var content = template.replace("{time}", LocalDateTime.now().toString()).getBytes();
//        outputStream.write((
//                "HTTP/1.1 200 OK\r\n" +
//                        "Content-Type: " + mimeType + "\r\n" +
//                        "Content-Length: " + content.length + "\r\n" +
//                        "Connection: close\r\n" +
//                        "\r\n"
//        ).getBytes());
//        outputStream.write(content);
//        outputStream.flush();
//    }

//    private void response(Path filePath, String mimeType) throws IOException {
//        final var length = Files.size(filePath);
//        outputStream.write((
//                "HTTP/1.1 200 OK\r\n" +
//                        "Content-Type: " + mimeType + "\r\n" +
//                        "Content-Length: " + length + "\r\n" +
//                        "Connection: close\r\n" +
//                        "\r\n"
//        ).getBytes());
//        Files.copy(filePath, outputStream);
//        outputStream.flush();
//    }

    private void disconnect(Socket socket, BufferedReader in, BufferedOutputStream out) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}

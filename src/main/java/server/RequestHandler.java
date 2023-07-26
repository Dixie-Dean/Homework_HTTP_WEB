package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedOutputStream out;

    public RequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            handleRequest();
        } catch (IOException e) {
            disconnect(socket, in, out);
        }
    }

    public void handleRequest() throws IOException {
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            disconnect(socket, in, out);
        }

        final var path = parts[1];
        if (!Server.getValidPaths().contains(path)) {
            out.write((
                    """
                            HTTP/1.1 404 Not Found\r
                            Content-Length: 0\r
                            Connection: close\r
                            \r
                            """
            ).getBytes());
            out.flush();
            disconnect(socket, in, out);
        }

        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            disconnect(socket, in, out);
        }

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

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

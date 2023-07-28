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
        final var parts = requestLine.split(" ");
        checkRequestLineLength(parts);

        final var request = new Request(parts[0], parts[1]);
        Handler handler = Server.getHandlers().get(request.getKey());
        if (handler != null) {
            handler.handle(request, outputStream);
        }
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

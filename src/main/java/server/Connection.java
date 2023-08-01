package server;

import handler.Handler;
import request.Request;
import request.RequestBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection implements Runnable {
    private final Socket socket;
    private final BufferedInputStream inputStream;
    private final BufferedOutputStream outputStream;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new BufferedInputStream(socket.getInputStream());
        this.outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try (socket; inputStream; outputStream){
            handleRequest();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void handleRequest() throws IOException {
        Request request = RequestBuilder.build(inputStream, outputStream);
        if (request != null) {

            String path = request.getPath();
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf('?'));
            }
            String key = request.getMethod() + "=" + path;
            Handler handler = Server.getHandlers().get(key);

            if (handler != null) {
                handler.handle(request, outputStream);
            }
        } else {
            disconnect(socket, inputStream, outputStream);
        }
    }

    private void disconnect(Socket socket, BufferedInputStream in, BufferedOutputStream out) {
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

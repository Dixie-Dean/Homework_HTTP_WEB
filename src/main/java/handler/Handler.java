package handler;

import request.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;

public interface Handler {
    void handle(Request request, BufferedOutputStream responseStream) throws IOException;
}

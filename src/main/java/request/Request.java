package request;

import java.util.HashMap;

public class Request {
    private final String method;
    private final String path;
    private final String body;
    private final HashMap<String, String> key;

    public Request(String method, String path, String body) {
        this.method = method;
        this.path = path;
        this.body = body;
        this.key = new HashMap<>();
        this.key.put(method, path);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HashMap<String, String> getKey() {
        return key;
    }
}

package request;

import java.util.HashMap;

public class Request {
    private final String method;
    private final String path;
    private final HashMap<String, String> key;

    public Request(String method, String path) {
        this.method = method;
        this.path = path;
        this.key = new HashMap<>();
        this.key.put(method, path);
    }
    public String getPath() {
        return path;
    }

    public HashMap<String, String> getKey() {
        return key;
    }
}

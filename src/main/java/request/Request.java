package request;

import java.util.HashMap;
import java.util.List;

public class Request {
    private final String method;
    private final String path;
    private final List<String> headers;
    private final String body;
    private final HashMap<String, String> key;

    public Request(String method, String path, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.key = new HashMap<>();
        this.key.put(method, path);
    }
    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public HashMap<String, String> getKey() {
        return key;
    }
}

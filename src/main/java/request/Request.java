package request;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;

public class Request {
    private final String method;
    private final String path;
    private final List<String> headers;
    private final String body;
    private final List<NameValuePair> params;

    public Request(String method, String path, List<NameValuePair> params, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.params = params;
        this.headers = headers;
        this.body = body;
    }

    public List<NameValuePair> getQueryParams() {
        return params;
    }

    public List<NameValuePair> getQueryParamsByName(String name) {
        return params.stream().filter(pair -> pair.getName().equals(name)).toList();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}

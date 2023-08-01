package request;

import org.apache.http.NameValuePair;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Request {
    private final String method;
    private String path;
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
        if (path.contains("?")) {
            String pathWithoutParams = path.substring(0, path.indexOf('?'));
            String paramsToEncode = path.substring(path.lastIndexOf('?'));
            URLEncoder.encode(paramsToEncode, StandardCharsets.UTF_8);
            return pathWithoutParams + paramsToEncode;
        } else {
            return path;
        }
    }

    public String getKey() {
        return getMethod() + "=" + getPath();
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
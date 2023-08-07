package request;

import org.apache.commons.fileupload.FileItem;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Request {
    private final String method;
    private String path;
    private final List<NameValuePair> queryParams;
    private final List<String> headers;
    private final String body;
    private final List<HashMap<String, String>> postParams;
    private final List<FileItem> parts;

    public Request(String method, String path,
                   List<NameValuePair> queryParams,
                   List<String> headers,
                   String body,
                   List<HashMap<String, String>> postParams,
                   List<FileItem> parts)
    {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
        this.postParams = postParams;
        this.parts = parts;
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

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<NameValuePair> getQueryParamsByName(String name) {
        return queryParams.stream().filter(pair -> pair.getName().equals(name)).toList();
    }

    public List<HashMap<String, String>> getPostParams() {
        return postParams;
    }

    public List<HashMap<String, String>> getPostParamByName(String name) {
        List<HashMap<String, String>> certainBodyParams = new ArrayList<>();
        for (HashMap<String, String> param : postParams) {
            if (param.containsKey(name)) {
                certainBodyParams.add(param);
            }
        }
        return certainBodyParams;
    }

    public List<FileItem> getParts() {
        return parts;
    }

    public String getKey() {
        return getMethod() + "=" + getPath();
    }


}
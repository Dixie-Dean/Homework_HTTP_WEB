package request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Request {
    private final String method;
    private String path;
    private final List<String> headers;
    private final String body;
    private List<NameValuePair> queryParams;
    private List<HashMap<String, String>> postParams;
    private List<Part> multipartParams;

    public Request(String method, String path, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
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

    public List<Part> getMultipartParams() {
        return multipartParams;
    }

    public List<Part> getMultipartParamByName(String name) {

    }

    public String getKey() {
        return getMethod() + "=" + getPath();
    }

    private void setQueryParams(List<NameValuePair> queryParams) {
        this.queryParams = queryParams;
    }

    public void parseQueryParams() {
        final List<NameValuePair> queryParams;
        try {
            queryParams = URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
            setQueryParams(queryParams);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static boolean isUrlencoded(List<String> headers) {
        return headers.contains("Content-Type: application/x-www-form-urlencoded");
    }

    private void setPostParams(List<HashMap<String, String>> postParams) {
        this.postParams = postParams;
    }

    public void parsePostParams() {
        if (isUrlencoded(headers) && body != null) {
            List<HashMap<String, String>> formParams = new ArrayList<>();
            String[] params = body.split("&");
            for (String pair : params) {
                String[] keyAndValue = pair.split("=");
                HashMap<String, String> pairs = new HashMap<>();
                pairs.put(keyAndValue[0], keyAndValue[1]);
                formParams.add(pairs);
            }
            setPostParams(formParams);
        }
    }

    private boolean isMultipart(List<String> headers) {
        return headers.contains("Content-Type: multipart/form-data");
    }

    private void setMultipartParams(List<Part> multipartParams) {
        this.multipartParams = multipartParams;
    }

    private List<FileItem> parseMultipartParams(String body) throws FileUploadException {
        RequestFileUpload fileUpload = new RequestFileUpload();
        try {
            FileItemIterator iterStream = fileUpload.getItemIterator(this);
            while (iterStream.hasNext()) {
                FileItemStream item = iterStream.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                Part part;
                if (!item.isFormField()) {
                    byte[] content = stream.readAllBytes();
                    part = new Part(false, content);
                } else {
                    String value = Streams.asString(stream);
                    part = new Part(true, value);
                }
                List<Part> listParts = this.multipartParams.computeIfAbsent(name, k -> new ArrayList<>());
                listParts.add(part);
            }
        } catch (FileUploadException | IOException e) {
            e.printStackTrace();
        }
    }
}
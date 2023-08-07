package request;

import multipart.Context;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.util.Streams;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Request {
    private final String method;
    private final String path;
    private final List<String> headers;
    private final String body;
    private List<NameValuePair> queryParams;
    private List<HashMap<String, String>> postParams;
    private final Map<String, List<Part>> multipartParams = new HashMap<>();
    private final String contentType;

    public Request(String method, String path, List<String> headers, String body, String contentType) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.contentType = contentType;
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

    public String getKey() {
        return getMethod() + "=" + getPath();
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

    public Map<String, List<Part>> getMultipartParams() {
        return multipartParams;
    }

    public List<Part> getMultipartParamByName(String name) {
        return multipartParams.get(name);
    }

    public void parseQueryParams() {
        final List<NameValuePair> queryParams;
        try {
            queryParams = URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
            this.queryParams = queryParams;
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
    }

    public void parsePostParams() {
        if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded") && body != null) {
            List<HashMap<String, String>> postParams = new ArrayList<>();
            String[] params = body.split("&");
            for (String pair : params) {
                String[] keyAndValue = pair.split("=");
                HashMap<String, String> pairs = new HashMap<>();
                pairs.put(keyAndValue[0], keyAndValue[1]);
                postParams.add(pairs);
            }
            this.postParams = postParams;
        }
    }

    public void parseMultipartParams() {
        if (contentType != null && contentType.startsWith("multipart/form-data") && body != null) {
            FileUpload fileUpload = new FileUpload();
            try {
                FileItemIterator iterStream = fileUpload.getItemIterator(new Context(body.getBytes(), contentType));
                while (iterStream.hasNext()) {
                    FileItemStream item = iterStream.next();
                    String name = item.getFieldName();
                    InputStream stream = item.openStream();
                    Part part;
                    if (!item.isFormField()) {
                        byte[] content = stream.readAllBytes();
                        part = new Part(content);
                    } else {
                        String value = Streams.asString(stream);
                        part = new Part(value);
                    }
                    List<Part> listParts = multipartParams.computeIfAbsent(name, k -> new ArrayList<>());
                    listParts.add(part);
                }
            } catch (FileUploadException | IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
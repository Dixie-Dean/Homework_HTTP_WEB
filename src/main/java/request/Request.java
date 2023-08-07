package request;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
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
    private Map<String, List<Part>> multipartParams;
    private final InputStream inputStream;
    private final String contentType;
    private final String contentLength;

    public Request(String method, String path,
                   List<String> headers, String body,
                   InputStream inputStream,
                   String contentType,
                   String contentLength)
    {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.contentLength = contentLength;
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

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentLength() {
        return contentLength;
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

    public Map<String, List<Part>> getMultipartParamByName(String name) {
        return null;
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

    private void setMultipartParams(Map<String, List<Part>> multipartParams) {
        this.multipartParams = multipartParams;
    }

    public void parseMultipartParams() {
        if (isMultipart(headers) && body != null) {
            HashMap<String, List<Part>> multipartParams = new HashMap<>();
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
                        part = new Part(content);
                    } else {
                        String value = Streams.asString(stream);
                        part = new Part(value);
                    }
                    List<Part> params = this.multipartParams.computeIfAbsent(name, k -> new ArrayList<>());
                    params.add(part);
                    multipartParams.put(name, params);
                    setMultipartParams(multipartParams);
                }
            } catch (FileUploadException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package request;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestBuilder {
    public static final String GET = "GET";
    public static final String POST = "POST";
    private static final List<String> allowedMethods = List.of(GET, POST);

    public static synchronized Request build(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        final var limit = 4096;
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        // ищем request line
        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            badRequest(out);
            return null;
        }

        // читаем request line
        final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            badRequest(out);
            return null;
        }

        final var method = requestLine[0];
        if (!allowedMethods.contains(method)) {
            badRequest(out);
            return null;
        }

        final var path = requestLine[1];
        if (!path.startsWith("/")) {
            badRequest(out);
            return null;
        }

        final List<NameValuePair> queryParams;
        try {
            queryParams = URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        // ищем заголовки
        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            badRequest(out);
            return null;
        }

        // отматываем на начало буфера
        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));

        String body = null;
        if (!method.equals("GET")) {
            in.skip(headersDelimiter.length);
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = in.readNBytes(length);
                body = new String(bodyBytes);
            }
        }

        List<HashMap<String, String>> bodyParams = null;
        if (checkContentType(headers) && body != null) {
            bodyParams = parseFormBody(body);
        }

        Request request = new Request(method, path, queryParams, headers, body, bodyParams);
        log(request);
        return request;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static void badRequest(BufferedOutputStream outputStream) throws IOException {
        outputStream.write((
                """
                        HTTP/1.1 400 Bad Request\r
                        Content-Length: 0\r
                        Connection: close\r
                        \r
                        """
        ).getBytes());
        outputStream.flush();
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static boolean checkContentType(List<String> headers) {
        return headers.contains("Content-Type: application/x-www-form-urlencoded");
    }

    private static List<HashMap<String, String>> parseFormBody(String body) {
        List<HashMap<String, String>> formParams = new ArrayList<>();
        String[] params = body.split("&");
        for (String pair : params) {
            String[] keyAndValue = pair.split("=");
            HashMap<String, String> pairs = new HashMap<>();
            pairs.put(keyAndValue[0], keyAndValue[1]);
            formParams.add(pairs);
        }
        return formParams;
    }

    private static void log(Request request) {
        if (request.getPath().equals("/favicon.ico")) {
            return;
        }

        System.out.println("Method: " + request.getMethod());

        System.out.println("Path: " + request.getPath());

        if (!request.getQueryParams().isEmpty()) {
            System.out.println("All Query Params: " + request.getQueryParams());
        }

        if (!request.getQueryParams().isEmpty()) {
            System.out.println("Query by Name: " + request.getQueryParamsByName("name"));
        }

        System.out.println("Headers: " + request.getHeaders());

        if (request.getBody() != null) {
            System.out.println("Body: " + request.getBody());
        }

        if (request.getPostParams() != null) {
            System.out.println("All Post Params: " + request.getPostParams());
        }

        if (request.getPostParams() != null) {
            System.out.println("Post Param by Name: " + request.getPostParamByName("name"));
        }

        System.out.println();
    }
}
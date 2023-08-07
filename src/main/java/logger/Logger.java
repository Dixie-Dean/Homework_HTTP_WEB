package logger;

import request.Request;

public class Logger {
    public static void logRequest(Request request) {
        if (request.getPath().equals("/favicon.ico")) {
            return;
        }
        System.out.println("Method: " + request.getMethod());
        System.out.println("Path: " + request.getPath());
        System.out.println("Headers: " + request.getHeaders());
        if (request.getBody() != null && !request.getBody().equals("")) {
            System.out.println("Body: " + request.getBody());
        }
        if (request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
            System.out.println("All Query Params: " + request.getQueryParams());
        }
        if (request.getQueryParams() != null && !request.getQueryParams().isEmpty()) {
            System.out.println("Query by Name: " + request.getQueryParamsByName("name"));
        }
        if (request.getPostParams() != null && !request.getPostParams().isEmpty()) {
            System.out.println("All Post Params: " + request.getPostParams());
        }
        if (request.getPostParams() != null && !request.getPostParams().isEmpty()) {
            System.out.println("Post Param by Name: " + request.getPostParamByName("name"));
        }
        if (request.getMultipartParams() != null && !request.getMultipartParams().isEmpty()) {
            System.out.println("Multipart params: " + request.getMultipartParams());
        }
        if (request.getMultipartParams() != null && !request.getMultipartParams().isEmpty()) {
            System.out.println("Multipart param by name: " + request.getMultipartParamByName("name"));
        }
        System.out.println();
    }
}

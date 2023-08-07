package logger;

import request.Request;

public class Logger {
    public static void logRequest(Request request) {
        if (request.getPath().equals("/favicon.ico")) {
            return;
        }
        System.out.println("Method: " + request.getMethod());
        System.out.println("Path: " + request.getPath());
        if (request.getQueryParams() != null) {
            System.out.println("All Query Params: " + request.getQueryParams());
        }
        if (request.getQueryParams() != null) {
            System.out.println("Query by Name: " + request.getQueryParamsByName("name"));
        }
        System.out.println("Headers: " + request.getHeaders());
//        if (request.getBody() != null) {
//            System.out.println("Body: " + request.getBody());
//        }
        if (request.getPostParams() != null) {
            System.out.println("All Post Params: " + request.getPostParams());
        }
        if (request.getPostParams() != null) {
            System.out.println("Post Param by Name: " + request.getPostParamByName("name"));
        }
        if (request.getMultipartParams() != null) {
            System.out.println("Multipart params: " + request.getMultipartParams());
        }
        if (request.getMultipartParams() != null) {
            System.out.println("Multipart param by name: " + request.getMultipartParamByName("name"));
        }
        System.out.println();
    }
}

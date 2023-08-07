package multipart;

import org.apache.commons.fileupload.UploadContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Context implements UploadContext {
    private final byte[] request;
    private final String contentType;

    public Context(byte[] requestBody, String contentTypeHeader) {
        this.request = requestBody;
        this.contentType = contentTypeHeader;
    }

    @Override
    public long contentLength() {
        return request.length;
    }

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public int getContentLength() {
        return request.length;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(request);
    }
}

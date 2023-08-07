package request;

import org.apache.commons.fileupload.UploadContext;

import java.io.InputStream;
import java.util.Arrays;

public class Context implements UploadContext {
    private final Request request;

    public Context(Request request) {
        this.request = request;
    }

    @Override
    public long contentLength() {
        return Long.parseLong(request.getContentLength());
    }

    @Override
    public String getCharacterEncoding() {
        if(request.getContentType() == null) {
            return null;
        } else {
            return Arrays.stream(request.getContentType().split(";"))
                    .filter(o -> o.startsWith("charset"))
                    .map(o -> o.substring(o.indexOf("=")))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public InputStream getInputStream() {
        return request.getInputStream();
    }

    @Override
    public int getContentLength() {
        return Integer.parseInt(request.getContentLength());
    }
}

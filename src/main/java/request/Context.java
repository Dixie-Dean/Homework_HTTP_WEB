package request;

import org.apache.commons.fileupload.UploadContext;

import java.io.IOException;
import java.io.InputStream;

public class Context implements UploadContext {

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}

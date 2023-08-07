package request;

import org.apache.commons.fileupload.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RequestFileUpload extends FileUpload {
    private static final String POST_METHOD = "POST";

    public static boolean isMultipartContent(Request request) {
        return POST_METHOD.equalsIgnoreCase(request.getMethod()) &&
                FileUploadBase.isMultipartContent(new Context(request));
    }

    public RequestFileUpload() {
    }

    public RequestFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    public List<FileItem> parseRequest(Request request) throws FileUploadException {
        return this.parseRequest(new Context(request));
    }

    public Map<String, List<FileItem>> parseParameterMap(Request request) throws FileUploadException {
        return this.parseParameterMap(new Context(request));
    }

    public FileItemIterator getItemIterator(Request request) throws FileUploadException, IOException {
        return super.getItemIterator(new Context(request));
    }
}

package server.http;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hasan Masum(hmasum52)
 * @since (1.0)2020-10-26
 */
public class HttpResponse {
    /// response codes
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int INTERNAL_SERVER_ERROR = 500;

    // response messages
    public static final String OK_MESSAGE = "OK";
    public static final String CREATED_MESSAGE = "Created";
    public static final String BAD_REQUEST_MESSAGE = "Bad Request";
    public static final String NOT_FOUND_MESSAGE = "Not Found";
    public static final String METHOD_NOT_ALLOWED_MESSAGE = "Method Not Allowed";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

    // http version
    public static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    //content types
    public static final String TEXT_HTML = "text/html; charset=UTF-8";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_GIF = "image/gif";


    private String version;
    private int statusCode;
    private String statusMessage;
    private String contentType;
    private long contentLength = 0;
    private String body = "";
    private Map<String, String> headers = new HashMap<String, String>();

    public HttpResponse setVersion(String version) {
        this.version = version;
        return this;
    }

    public HttpResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse setContentLength(long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public HttpResponse setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponse addHeader(String key, String value) {
        this.headers.put(key.toLowerCase(), value);
        return this;
    }

    public String getResponse() {
        // http response
        String res = version + " " + statusCode + " " + statusMessage + "\n"
                + "content-type: "+contentType+"\n"
                + "content-length: " + contentLength + "\n";

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            res += entry.getKey() + ": " + entry.getValue() + "\n";
        }

        res += "connection: close\n\n" + body;
        return res;
    }
}

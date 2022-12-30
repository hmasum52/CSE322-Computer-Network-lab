package server.http;
/**
 * @author Hasan Masum(hmasum52)
 * @since (1.0)2020-10-26
 */
public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, CONNECT;

    public static HttpMethod map(String method){
        switch(method){
            case "GET":
                return GET;
            case "POST":
                return POST;
            case "PUT":
                return PUT;
            case "DELETE":
                return DELETE;
            case "HEAD":
                return HEAD;
            case "OPTIONS":
                return OPTIONS;
            case "TRACE":
                return TRACE;
            case "CONNECT":
                return CONNECT;
            default:
                return null;
        }
    }
}

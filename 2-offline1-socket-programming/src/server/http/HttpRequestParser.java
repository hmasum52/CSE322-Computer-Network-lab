package server.http;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import log.Log;

/**
 * @author Hasan Masum(hmasum52)
 * @since (1.0)2020-10-26
 */
public class HttpRequestParser {
    public static int CHUNK_SIZE = 4096;

    private HttpMethod method;
    private String path;
    private String version;
    private String body;
    private Map<String, String> headers = new HashMap<String, String>();

    public HttpRequestParser(Socket clientSocket) throws IOException, Exception{
        this(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
        
    }

    public HttpRequestParser(BufferedReader in) throws Exception{
        // step 2
        // get request from client
        // get the header
        String header = readHeader(in);
        Log.info(header);
        parseHeader(header);    
        // get the body
        if (headers.containsKey("content-length")) {
            int contentLength = Integer.parseInt(headers.get("content-length"));
            char[] buffer = new char[CHUNK_SIZE];
            // loop to read the body
            body = "";
            while (contentLength > 0) {
                int read = in.read(buffer, 0, Math.min(CHUNK_SIZE, contentLength));
                body += new String(buffer, 0, read);
                contentLength -= read;
            }
            Log.info(body);
        } else {
            Log.info("No body");
        }
    }

    private void parseHeader(String header){
        String[] lines = header.split("\n");
        String[] firstLine = lines[0].split(" ");
        this.method = HttpMethod.map(firstLine[0]);
        this.path = firstLine[1];
        this.version = firstLine[2];

        for (int i = 1; i < lines.length; i++) {
            String[] line = lines[i].split(": ");
            String key = line[0];
            String value = line[1];
            this.headers.put(key.toLowerCase(), value);
        }
    }

    private String readHeader(BufferedReader in) throws Exception {
        String line = in.readLine();
        String header = "";
        while (line != null && !line.isEmpty()) {
            header += line + "\n";
            line = in.readLine();
        }
        return header;
    }

    // getters
    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    // setters
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "HttpRequest [body=" + body + ", headers=" + headers + ", method=" + method + ", path=" + path
                + ", version=" + version + "]";
    }

}

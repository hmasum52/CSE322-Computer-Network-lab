package server;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import log.Log;
import server.http.HttpRequestParser;
import server.http.HttpResponse;

/**
 * @author Hasan Masum(hmasum52)
 * @since (1.0)2020-10-26
 */
public class WebServerThread extends Thread {

    private final String serverRoot;

    // client socket field
    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedOutputStream out;
    private final PrintWriter writer;

    public WebServerThread(Socket clientSocket, String serverRoot) throws IOException {
        this.clientSocket = clientSocket;
        this.serverRoot = serverRoot;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new BufferedOutputStream(clientSocket.getOutputStream());
        this.writer = new PrintWriter(out, true);
    }

    @Override
    public void run() {
        try {
            //HttpRequestParser parser = new HttpRequestParser(in);
            HttpRequestParser parser = new HttpRequestParser(clientSocket);

            serve(parser.getPath());

        } catch (Exception e) {
            Log.severe(e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                writer.close();
                clientSocket.close();
            } catch (IOException e) {
                Log.severe(e.getMessage());
            }
        }
    }

    /**
     * Serve the http client
     * 
     * @param path
     */
    private void serve(String path) {
        File fileContent = new File(path.isEmpty() ? serverRoot : serverRoot + "/" + path);
        Log.info("Serving path: " + fileContent.getAbsolutePath());
        File[] contents = fileContent.listFiles();

        HttpResponse response = new HttpResponse();

        response.addHeader("Server", WebServer.SERVER_NAME)
                .addHeader("Date", new Date().toString());

        // make hello.html
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>Hello</title>");
        // avoid favicon
        sb.append("<link rel=\"icon\" href=\"data:;base64,iVBORw0KGgo=\">");
        sb.append("</head>");
        sb.append("<body>");

        if (fileContent.exists() && fileContent.isDirectory()) {
            // directory empty message
            if (contents == null || contents.length == 0) {
                sb.append("<h1>Directory is empty</h1>");
            }

            for (File f : contents) {
                if (f.isDirectory()) {
                    sb.append("<b><i><a href=\"" + f.getName() + "/\">" + f.getName() + "/</a></i></b><br>");
                } else if (f.isFile()) {
                    sb.append(
                            "<a href=\"" + f.getName() + "\">" + f.getName() + "</a><br>");
                }
            }
            // response ok
            response.setVersion(HttpResponse.HTTP_VERSION_1_1)
                    .setStatusCode(HttpResponse.OK)
                    .setStatusMessage(HttpResponse.OK_MESSAGE);

        } else if (fileContent.exists() && fileContent.isFile()) {
            // set content type
            String ext = fileContent.getName().substring(fileContent.getName().lastIndexOf("."));
            String contentType = getMimeType(ext);
            String inlineOrAttachment = viewOrDownload(contentType);

            // force download
            response.setVersion(HttpResponse.HTTP_VERSION_1_1)
                    .setStatusCode(HttpResponse.OK)
                    .setStatusMessage(HttpResponse.OK_MESSAGE)
                    .setContentType(contentType)
                    .addHeader("Content-Disposition", inlineOrAttachment+"; filename=\"" + fileContent.getName() + "\"")
                    .addHeader("Content-Transfer-Encoding", "binary")
                    .setContentLength(fileContent.length());

            // send response
            writer.print(response.getResponse());
            Log.info(response.getResponse());
            writer.flush();

            Log.info("Sending file: " + fileContent.getName());
            Log.info("file type: " + fileContent.getName().substring(fileContent.getName().lastIndexOf(".")));

            // send file in buffer chunks
            try {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileContent));
                byte[] bytes = new byte[1024];
                int byteReadCount = 0;
                while ((byteReadCount = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, byteReadCount);
                    out.flush();
                }
                inputStream.close();
                Log.info("File sent");
            } catch (IOException e) {
                Log.severe(e.getMessage());
            }

            
            return;
        } else {
            // 404 not found
            sb.append("<h1>404 Not Found</h1>");

            response.setStatusCode(HttpResponse.NOT_FOUND)
                    .setStatusMessage(HttpResponse.NOT_FOUND_MESSAGE);
        }

        sb.append("</body>");
        sb.append("</html>");

        // set response body
        response.setBody(sb.toString())
                .setContentLength(sb.toString().length());

        // send response
        writer.print(response.getResponse());
        Log.info(response.getResponse());
        writer.print("\r\n");
        writer.flush();
        Log.info("Response sent");
    }

    private String viewOrDownload(String contentType){
        if(contentType.startsWith("image") 
        || contentType.startsWith("text")
        || contentType.startsWith("application/json")
        || contentType.startsWith("application/xml")){
            return "inline";
        }
        return "attachment";
    }

    /**
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
     * 
     * @param fileExtension is the file exention
     * @return the mime type of the file
     */
    private String getMimeType(String fileExtension){
        switch(fileExtension){
            case ".html":
                return "text/html";
            case ".css":
                return "text/css";
            case ".mjs":
            case ".js":
                return "text/javascript";
            case ".csv":
                return "text/csv";
            case ".jpg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".svg":
                return "image/svg+xml";
            case ".ico":
                return "image/x-icon";
            case ".tif":
            case ".tiff":
                return "image/tiff";
            case "webp":
                return "image/webp";
            
            case ".pdf":
                return "application/pdf";
            case ".zip":
                return "application/zip";
            case ".rar":
                return "application/x-rar-compressed";
            case ".7z":
                return "application/x-7z-compressed";
            case ".mp3":
                return "audio/mpeg";
            case ".mp4":
                return "video/mp4";
            case ".wav":
                return "audio/wav";
            case ".avi":
                return "video/x-msvideo";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "ls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".ppt":
                return "application/vnd.ms-powerpoint";
            case ".pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case ".xml":
                return "application/xml";
            case ".json":
                return "application/json";
            case ".txt":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }
}
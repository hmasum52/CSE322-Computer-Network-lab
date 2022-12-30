package server;
import java.io.IOException;
import java.net.ServerSocket;

import log.Log;

/**
 * @author Hasan Masum(hmasum52)
 * @since (1.0)2020-10-26
 */
public class WebServer {
    // server name
    public static final String SERVER_NAME = "Java Web Server";

    public static void main(String[] args) throws IOException {
        String root = "G:/level-3-term-2/CSE-322-CNET-Lab/cse-322-cnet-codes/2-offline1-socket-programming";

        try (ServerSocket serverSocket = new ServerSocket(5052)) {
            Log.info("Server started on port 5052");
            while(true){
                new WebServerThread(serverSocket.accept(), root).start();
            }
        }catch(Exception e){
            Log.severe(e.getMessage());
        }
    }
}

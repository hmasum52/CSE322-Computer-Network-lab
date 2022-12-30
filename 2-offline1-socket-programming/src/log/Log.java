package log;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Hasan Masum(hmasum52)
 * @since (1.0)2020-10-26
 */
public class Log {
    private static Logger logger;
    private FileHandler fh;

    private Log(String fileName) throws IOException {
        File f = new File( fileName);
        if(!f.exists()){
            f.createNewFile();
        }
        fh = new FileHandler(fileName);
        logger = Logger.getLogger("MyLog");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }

    public synchronized static Logger logger(){
        if(logger == null){
            try {
                new Log("log.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }

    public synchronized static void info(String msg){
        logger().info("\n"+msg+"\n");
    }

    public synchronized static void warning(String msg){
        logger().warning("\n" +msg); 
    }

    public synchronized static void severe(String msg){
        logger().severe("\n" +msg+"\n"); 
    }

    public synchronized static void debug(String msg){
        logger().log(Level.ALL, msg);
    }
}

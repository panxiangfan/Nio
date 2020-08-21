package Nio.sample;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;  
  
public class NioClient {
      
    private final static Logger logger = Logger.getLogger(NioClient.class.getName());  
      
    public static void main(String[] args) {  

        new Thread(new MyRunnable(new NioClientHandler())).start();
    }  
      
    private static final class MyRunnable implements Runnable {
          
        private static final String FILEPATH =  "D:\\内网通文件\\超级管理员\\linux\\linuxIso\\CentOS-6.8-x86_64-bin-DVD1.iso";
          
        private NioClientHandler handler;  
        private MyRunnable(NioClientHandler handler) {  

            this.handler = handler;
        }  
  
        public void run() {  
            SocketChannel socketChannel = null;  
                try {  
                    socketChannel = SocketChannel.open();  
                      
                    SocketAddress socketAddress = new InetSocketAddress(  
                            InetAddress.getLocalHost(), 8080);  
                      
                    socketChannel.connect(socketAddress);  
                      
                    long start = System.currentTimeMillis();  
                    handler.sendData(socketChannel, FILEPATH, "CentOS-6.8-x86_64-bin-DVD1.iso");  

                } catch (IOException e) {  
                    logger.log(Level.SEVERE, null, e);  
                } finally {  
                    try {
                        socketChannel.close();  
                    } catch (Exception ex) {  
                    }  
                }  
        }  
    }  
}  
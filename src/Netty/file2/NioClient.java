package Netty.file2;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;  
  
//https://my.oschina.net/yybear/blog/201297 netty4 实现一个断点上传大文件功能
public class NioClient {  
      
    private final static Logger logger = Logger.getLogger(NioClient.class.getName());  
      
    public static void main(String[] args) {  

        new Thread(new MyRunnable(new NioClientHandler())).start();
    }  
      
    private static final class MyRunnable implements Runnable {  
          
        private static final String FILEPATH =  "F:\\下载\\ldinst_3.96.0.exe";  
          
        private NioClientHandler handler;  
        private MyRunnable(NioClientHandler handler) {  

            this.handler = handler;
        }  
  
        public void run() {

            SocketChannel socketChannel = null;  
                try {  
                    socketChannel = SocketChannel.open();  
                      
                    SocketAddress socketAddress = new InetSocketAddress(  
                            InetAddress.getLocalHost(), 5656);
                      
                    socketChannel.connect(socketAddress);  
                      
                    long start = System.currentTimeMillis();  
                    handler.sendData(socketChannel, FILEPATH, "ldinst_3.96.0.exe");
                    String response = handler.receiveData(socketChannel);  
                      
                    long end = System.currentTimeMillis();  
                    logger.log(Level.INFO, response + " 发送成功 : " + (end-start) + "ms");
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
package sample;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;  
  
//https://my.oschina.net/yybear/blog/201297 �ϵ�����ļ�
public class NioClient2 {  
      
    private final static Logger logger = Logger.getLogger(NioClient2.class.getName());  
      
    public static void main(String[] args) {  
        new Thread(new MyRunnable(new NioClientHandler())).start();  
    }  
      
    private static final class MyRunnable implements Runnable {  
          
        private static final String FILEPATH =  "C:\\Users\\Administrator\\Desktop\\b\\rhel-server-7.0-x86_64-dvd.iso";  
          
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
                    handler.sendData(socketChannel, FILEPATH, "rhel-server-7.0-x86_64-dvd.iso");  
                    //String response = handler.receiveData(socketChannel);  
                      
                    //long end = System.currentTimeMillis();  
                   // logger.log(Level.INFO, response + " ��ʱ : " + (end-start) + "ms");  
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
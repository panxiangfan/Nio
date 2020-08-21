package Nio.file;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerSocket {  
    public static void main(String[] args) throws Exception {  
        int port = 8000;  
          
        //打开服务器套接字通道  
        ServerSocketChannel ssc = ServerSocketChannel.open();  
        //创建一个选择器  
        Selector selector = Selector.open();  
        //设置非阻塞模式  
        ssc.configureBlocking(false);  
        InetSocketAddress address = new InetSocketAddress(port);  
        //绑定监听端口  
        ssc.bind(address);  
        //注册选择器，保持等待模式  
        ssc.register(selector, SelectionKey.OP_ACCEPT);  
        System.out.println("服务器已开启,端口："+port);  
        while(true){  
            selector.select();  
            //返回此选择器的已选择键集  
            Set<SelectionKey> keys=selector.selectedKeys();  
            Iterator<SelectionKey> iterKey=keys.iterator();  
              
            while(iterKey.hasNext()){  
                SelectionKey sk=iterKey.next();  
                //测试此键的通道是否已准备好接受新的套接字连接  
                if(sk.isAcceptable()){  
                    SocketChannel sc=ssc.accept();  
                    try {  
                        //接收  
                        new ReceiveAndSend().receiveFile(sc);
                        sc.close();  
                    } catch (Exception e) {  
                    }  
                }  
            }  
        }  
    }  
      
}  
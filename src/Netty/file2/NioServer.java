package Netty.file2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class NioServer {  
    Selector selector = null;  
    ServerSocketChannel serverSocketChannel = null;  
    private NioserverHandler handler;  
    public NioServer() throws IOException {  
        selector  = Selector.open();  
        // 打开服务器套接字通道  
        serverSocketChannel = ServerSocketChannel.open();  
          
        // 调整通道的阻塞模式非阻塞  
        serverSocketChannel.configureBlocking(false);  
        serverSocketChannel.socket().setReuseAddress(true);  
        serverSocketChannel.socket().bind(new InetSocketAddress(5656));
          
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  
    }  
      
    public NioServer(NioserverHandler handler) throws IOException {  
          
        this();  
        this.handler = handler;       
        while(selector.select() > 0) {  
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();  
            while(it.hasNext()) {  
                SelectionKey s = it.next();  
                it.remove();  
                this.handler.excute((ServerSocketChannel)s.channel());  
            }  
        }  
    }  
      
    public static void main(String[] args) throws IOException {  
        new NioServer(new NioserverHandler());  
    }  
}  
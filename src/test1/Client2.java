package test1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client2 {
	public static void main(String[] args)throws Exception {
		
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("192.168.1.57", 53132));
        socketChannel.configureBlocking(false);  
        
        
        Selector selector = Selector.open();  
        socketChannel.register(selector, SelectionKey.OP_READ);  
        
        
        ByteBuffer writeBuffer = ByteBuffer.wrap("linbo".getBytes("UTF-16"));  
        
        socketChannel.write(writeBuffer);  
        
        
        
        while (selector.select() > 0) {  
            for (SelectionKey sk : selector.selectedKeys()) {  
            	

            	
            	
            	 // 如果该SelectionKey对应的Channel中有可读的数据  
                if (sk.isReadable()) {  
                    // 使用NIO读取Channel中的数据  
                    SocketChannel sc = (SocketChannel) sk.channel();  
                    ByteBuffer buffer = ByteBuffer.allocate(1024);  
                    sc.read(buffer);  
                    buffer.flip();  

                    // 将字节转化为为UTF-16的字符串  
                    String receivedString = Charset.forName("UTF-16").newDecoder().decode(buffer).toString();  

                    // 控制台打印出来  
                    System.out.println("接收到来自服务器" + sc.socket().getRemoteSocketAddress() + "的信息:" + receivedString);  

                    // 为下一次读取作准备  
                    sk.interestOps(SelectionKey.OP_READ);  
                    
                    Thread.sleep(3 * 1000);
                    
                    writeBuffer = ByteBuffer.wrap("linbo".getBytes("UTF-16"));  
                    
                    socketChannel.write(writeBuffer); 
                }  

                // 删除正在处理的SelectionKey  
                selector.selectedKeys().remove(sk);  
                
                
                
                
            }  
        } 
	}
}

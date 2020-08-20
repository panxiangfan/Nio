package test1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws Exception {

        //获得一个socket通道  客户端连接服务器 需要调用channel.finishConnect();才能完成连接
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("192.168.1.57", 53132));
        //设置通道为非阻塞
        socketChannel.configureBlocking(false);
        //获得一个通道管理器
        Selector selector = Selector.open();
        //将通道管理器喝该听到绑定 并为通道注册Selectionkey.OP_READ事件
        socketChannel.register(selector, SelectionKey.OP_READ);

        //向服务端发送消息
        ByteBuffer writeBuffer = ByteBuffer.wrap("longchaoyang".getBytes("UTF-16"));
        //发送
        socketChannel.write(writeBuffer);

        //判定是否有选择器
        while (selector.select() > 0) {
            for (SelectionKey sk : selector.selectedKeys()) {  //返回此选择器的的选择键集

                // 如果该SelectionKey对应的Channel中有可读的数据
                if (sk.isReadable()) {
                    // 使用NIO读取Channel中的数据  
                    SocketChannel sc = (SocketChannel) sk.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);  //字节缓冲数
                    sc.read(buffer);
                    buffer.flip(); //

                    // 将字节转化为为UTF-16的字符串  
                    String receivedString = Charset.forName("UTF-16").newDecoder().decode(buffer).toString();

                    // 控制台打印出来  
                    System.out.println("接收到来自服务器" + sc.socket().getRemoteSocketAddress() + "的信息:" + receivedString);

                    // 为下一次读取作准备  
                    sk.interestOps(SelectionKey.OP_READ);

                    Thread.sleep(5 * 1000);

                    //发送给服务端
                    writeBuffer = ByteBuffer.wrap("longchaoyang".getBytes("UTF-16"));
                    //发送
                    socketChannel.write(writeBuffer);
                }

                // 删除正在处理的SelectionKey  
                selector.selectedKeys().remove(sk);

            }
        }
    }
}

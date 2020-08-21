package Nio.test1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) throws Exception {
        //打开一个选择器
        Selector selector = Selector.open();
        //获得一个选择器通道
        ServerSocketChannel listenerChannel = ServerSocketChannel.open();
        //将该通道对于的选择器绑定到port端口
        listenerChannel.socket().bind(new InetSocketAddress(53132));
        //设置通道为非阻塞
        listenerChannel.configureBlocking(false);
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
        //当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
        listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {

            int keys = selector.select(3000);

            if (keys == 0) {//如果没有连接数 就一直等待连接
                System.out.println("独自等待.");
                continue;
            }

            //返回此选择器的选择键集
            Set<SelectionKey> set = selector.selectedKeys();
            //设置一个迭代器
            Iterator<SelectionKey> keyIter = set.iterator();



            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next();

                try {
                    if (key.isAcceptable()) {//连接事件
                        //注册事件

                        SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                        clientChannel.configureBlocking(false);

                        clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                    }

                    if (key.isReadable()) {

                        // 获得与客户端通信的信道
                        SocketChannel clientChannel = (SocketChannel) key.channel();

                        // 得到并清空缓冲区
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        buffer.clear();

                        // 读取信息获得读取的字节数
                        long bytesRead = clientChannel.read(buffer);

                        if (bytesRead == -1) {
                            // 没有读取到内容的情况
                            clientChannel.close();
                        } else {
                            // 将缓冲区准备为数据传出状态
                            buffer.flip();

                            // 将字节转化为为UTF-16的字符串
                            String receivedString = Charset.forName("UTF-16").newDecoder().decode(buffer).toString();

                            // 控制台打印出来
                            System.out.println("接收到来自" + clientChannel.socket().getRemoteSocketAddress() + "的信息:" + receivedString);
                        }


                        //发消息
                        String sendString = "你好,客户端.";
                        buffer = ByteBuffer.wrap(sendString.getBytes("UTF-16"));
                        clientChannel.write(buffer);

                    }


                    if (key.isValid() && key.isWritable()) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // 移除处理过的键
                keyIter.remove();
            }
        }

    }
}

package NioWork.manyFileupload;


import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NioClientThread extends Thread{
    private  String basePath=null;
    private  String filePath=null;
    public NioClientThread(String filePath, String basePath){
        this.basePath=basePath;
        this.filePath=filePath;
    }

    @Override
    public void run() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress("192.168.1.57", 8080);
            socketChannel.connect(socketAddress);
            cilentFile(socketChannel,filePath,basePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void cilentFile(SocketChannel socketChannel,String filePath,String basePath){
        File file=new File(filePath);
        try {
            if (file.isDirectory()){
                    //获取全路径
                    String path = filePath.replace(basePath, "");

                    //发送一个mark值 1
                    ByteBuffer buffer0 = ByteBuffer.allocate(4);
                    buffer0.putInt(1);

                    buffer0.flip();
                    socketChannel.write(buffer0);// 发送
                    buffer0.clear();

                    // 文件名长度
                    ByteBuffer buffer5 = ByteBuffer.allocate(4);
                    buffer5.putInt(new String(path.getBytes(), "ISO-8859-1").length());

                    buffer5.flip();
                    socketChannel.write(buffer5);// 发送
                    buffer5.clear();

                    ByteBuffer buffer6 = ByteBuffer.allocate(new String(path.getBytes(), "ISO-8859-1").length());
                    buffer6.put(path.getBytes());
                    buffer6.flip();
                    socketChannel.write(buffer6);// 发送
                    buffer6.clear();



            }else {
                //替换全路径
                String path = filePath.replace(basePath + "\\", "");
                //发送一个mark值 2
                ByteBuffer buffer0 = ByteBuffer.allocate(4);
                buffer0.putInt(2);
                buffer0.flip();
                socketChannel.write(buffer0);// 发送
                buffer0.clear();

                // 文件名长度
                ByteBuffer buffer1 = ByteBuffer.allocate(4);
                buffer1.putInt(new String(path.getBytes(), "ISO-8859-1").length());

                buffer1.flip();
                socketChannel.write(buffer1);// 发送
                buffer1.clear();

                ByteBuffer buffer2 = ByteBuffer.allocate(new String(path.getBytes(), "ISO-8859-1").length());
                buffer2.put(path.getBytes());
                buffer2.flip();
                socketChannel.write(buffer2);// 发送
                buffer2.clear();

                ByteBuffer buffer3 = ByteBuffer.allocate(8);
                long fileContentLength = new File(filePath).length();
                buffer3.putLong(fileContentLength);
                buffer3.flip();
                socketChannel.write(buffer3);// 发送
                buffer3.clear();

                ByteBuffer buffer4 = ByteBuffer.allocate(1024 * 1024);
                FileInputStream fileInputStream = new FileInputStream(new File(filePath));
                FileChannel fileChannel = fileInputStream.getChannel();
                long nowReadLength = 0;// 每次读取的文件内容长度
                long sumReadLength = 0;// 累加长度
                do {
                    nowReadLength = fileChannel.read(buffer4);
                    sumReadLength += nowReadLength;
                    buffer4.flip();
                    socketChannel.write(buffer4);
                    buffer4.clear();
                } while (nowReadLength != -1 && sumReadLength < fileContentLength);
                    fileChannel.close();
                    socketChannel.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
                NioClient.mark--;
        }
    }
}

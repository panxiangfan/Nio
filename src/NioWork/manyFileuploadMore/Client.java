package NioWork.manyFileuploadMore;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class Client {

    //上传的路径
    private static String basePath = "E:\\服务器";

    public static void main(String[] args) {
        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("192.168.1.57", 6666));

            long start = System.currentTimeMillis();
            upload(socketChannel, basePath);

            try {
                Thread.sleep(60*1000*60);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            }
        }


    public static void upload(SocketChannel socketChannel, String filepath) throws IOException {
        File file = new File(filepath);

        if (file.isDirectory()) {//判断是否为文件夹
            //不发送最基础目录
            if (!filepath.equals(file)) {

                //获取全路径
                String pathAll = file.getPath();
                //替换全路径
                String path = pathAll.replace(basePath, "");

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

            }
            //递归
            String files[] = file.list();
            for (String fPath : files) {
                upload(socketChannel, filepath + File.separator + fPath);
            }
        } else {
            //获取全路径
            String pathAll = file.getPath();
            //替换全路径
            String path = pathAll.replace(basePath + "\\", "");
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
            long fileContentLength = new File(filepath).length();
            buffer3.putLong(fileContentLength);
            buffer3.flip();
            socketChannel.write(buffer3);// 发送
            buffer3.clear();

            ByteBuffer buffer4 = ByteBuffer.allocate(1024 * 1024);
            FileInputStream fileInputStream = new FileInputStream(new File(filepath));
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
            System.out.println(filepath + "发送成功...");

        }

    }

}

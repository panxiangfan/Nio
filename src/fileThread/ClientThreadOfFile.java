package fileThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ClientThreadOfFile extends Thread {
    private String path = null;
    private File file = null;

    public ClientThreadOfFile(File file, String path) {
        this.file = file;
        this.path = path;
    }

    public void run() {
        SocketChannel socketChannel = null;
        FileInputStream fileInputStream = null;
        FileChannel fileChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("192.168.1.57", 6666));

            /* 寰楀埌鏂囦欢鍚? */
            String filePath = file.getPath();
            filePath = filePath.replace(path, "");

            /* 鍙戦?佹枃浠跺悕闀垮害銆佹枃浠跺悕銆佹爣璇? */
            ByteBuffer fileNameAndTypeBuffer = ByteBuffer.allocate(4 + new String(filePath.getBytes(), "ISO-8859-1").length() + 4);
            fileNameAndTypeBuffer.putInt(new String(filePath.getBytes(), "ISO-8859-1").length());//鏂囦欢鍚嶉暱搴?
            fileNameAndTypeBuffer.put(filePath.getBytes());//鏂囦欢鍚?
            fileNameAndTypeBuffer.putInt(0);//鏍囪瘑鏂囦欢
            fileNameAndTypeBuffer.flip();
            socketChannel.write(fileNameAndTypeBuffer);
            fileNameAndTypeBuffer.clear();//鎶婂綋鍓嶄綅缃疆涓?0

            /* 鍙戦?佹枃浠跺唴瀹归暱搴? */
            ByteBuffer fileContentLengthBuffer = ByteBuffer.allocate(8);
            fileContentLengthBuffer.putLong(file.length());
            fileContentLengthBuffer.flip();
            socketChannel.write(fileContentLengthBuffer);
            fileContentLengthBuffer.clear();

            /* 鍙戦?佹枃浠跺唴瀹? */
            ByteBuffer fileContentBuffer = ByteBuffer.allocate(1024 * 1024 * 5);
            long fileContentLength = file.length();
            int nowReadLength = 0;
            long sumReadLength = 0;
            fileInputStream = new FileInputStream(file);
            fileChannel = fileInputStream.getChannel();
            do {
                nowReadLength = fileChannel.read(fileContentBuffer);
                sumReadLength += nowReadLength;
                fileContentBuffer.flip();
                socketChannel.write(fileContentBuffer);
                fileContentBuffer.clear();
                System.out.println(nowReadLength + "  " + sumReadLength + "  " + fileContentLength);

            } while (nowReadLength != -1 && sumReadLength < fileContentLength);

            System.out.println("鍙戦?佹垚鍔燂細" + file.getPath());
            fileChannel.close();
            fileInputStream.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (socketChannel != null) {
                    socketChannel.close();
                    Client.number--;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

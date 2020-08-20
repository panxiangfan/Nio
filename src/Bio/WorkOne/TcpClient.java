package Bio.WorkOne;

import java.io.*;
import java.net.Socket;

/**
 * 客户端
 *
 * @author 平桑
 * @date 2020-8-8
 *
 */
public class TcpClient {

    /**
     * 为了方便 下面用静态方法
     */
    private static String basePath = "E:\\Program Files\\";
    private static Socket socket;
    private static DataOutputStream dataOutput;

    public static void main(String[] args) throws Exception {//把异常抛出
        socket = new Socket("192.168.1.57", 6678);// 创建客户端Socket,指定服务器地址和端口,端口必须和服务端一致
        dataOutput = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));// 由Socket对象得到输入流,并构造相应的DataOutputStream对象
        TcpClient.upload(basePath);

    }

    public static void upload(String filePath) throws Exception {
        File file = new File(filePath);
        if (file.isDirectory())// 判断是否为文件夹
        {
            //不发送最基础目录
            if (!filePath.equals(basePath)) {
                dataOutput.writeInt(1);// 表示文件夹
                // 获取文件全路径
                String fileAllPath = file.getPath();
                // 替换基础路径
                String path = fileAllPath.replace(basePath, "");
                // 发送路径长度
                dataOutput.writeInt(new String(path.getBytes(), "ISO-8859-1").length());
                // 发送路径
                dataOutput.write(path.getBytes());
                dataOutput.flush();
            }
            //递归
            String files[] = file.list();
            for (String fPath : files) {
                upload(filePath + File.separator + fPath);
            }
        } else {
            dataOutput.writeInt(2);// 表示文件
            // 获取文件全路径
            String fileAllPath = file.getPath();
            // 替换基础路径
            String path = fileAllPath.replace(basePath, "");
            // 发送路径长度
            dataOutput.writeInt(new String(path.getBytes(), "ISO-8859-1").length());

            // 发送路径
            dataOutput.write(path.getBytes());

            long fileLength = file.length();
            dataOutput.writeLong(fileLength);

            // 发送文件内容
            DataInputStream dataInput = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            byte[] byt = new byte[1024 * 1024 * 20];// new一个byte数组
            int len = 0;

            while ((len = dataInput.read(byt)) > 0) {// 只要文件内容的长度大于0,就一直读取

                dataOutput.write(byt, 0, len);// 将读取的文件内容写过去
                dataOutput.flush();
            }
            System.out.println(filePath + "发送完成了...");
            dataInput.close();
        }
    }
}

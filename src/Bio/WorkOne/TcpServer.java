package Bio.WorkOne;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    String basePath = "C:\\Users\\Administrator\\Desktop\\Socket测试\\";

    public static void main(String[] args) throws Exception {//下面抛异常了 这里也要
        TcpServer server = new TcpServer();
        int port = 6678;// 定义一个端口,和客户端保持一致
        server.upload(port);
    }

    /**
     * 服务端文件读取下载
     *
     * @param port
     */
    public void upload(int port) throws Exception {//这里把异常抛出 避免太多繁琐的try catch

        ServerSocket serverSocket = null;
        Socket socket = null;
        DataInputStream dataInput = null;
        DataOutputStream dataOutput = null;


        serverSocket = new ServerSocket(port);// 构建一个Serversocket
        socket = serverSocket.accept();// 监听客户端
        System.out.println("客户端" + socket.getInetAddress() + "已连接");
        dataInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        /**
         * 得到文件名  设置标识
         * 文件夹 1  文件 2
         */
        while (true) {
            int mark = 0;
            while (true) {
                if (dataInput.available() >= 4) {
                    mark = dataInput.readInt(); // 得到客户端写过来的文件长度,　　　4个字节
                    break;
                }
            }

            //文件夹
            if (mark == 1) {
                //文件路径的长度
                int filePathlengt = 0;
                while (true) {
                    if (dataInput.available() >= 4) { //保证长度够用
                        filePathlengt = dataInput.readInt();// 得到客户端来的文件长度 int 4个字节
                        break;
                    }
                }
                //获取文件路径
                byte path[] = new byte[filePathlengt]; //将路径换成byte数组
                String filePath = null;
                int pathLength = 0;
                while (true) { //保证文件名长度全部读取完
                    pathLength += dataInput.read(path);
                    if (filePathlengt == pathLength) {
                        break;
                    }
                }

                filePath = new String(path);

                //得到文件夹的路径 如果不存在就创造
                File file = new File(basePath + filePath);// 得到(盘符+文件夹名 )绝对路径
                if (!file.exists()) {//如果不存在
                    file.mkdirs();//创建文件夹
                }
            }

            //文件
            else if (mark == 2) {

                int filePathlength = 0;
                while (true) {
                    if (dataInput.available() >= 4) {//跟获取文件夹一样 但是不用考虑递归问题
                        filePathlength = dataInput.readInt();//得到客户端的文件长度 int 4个字节
                        break;
                    }
                }

                byte name[] = new byte[filePathlength]; // 转换成byte数组
                String filePath = null;
                int nameLength = 0;

                while (true) {// 直到将文件名的长度全部读取完成
                    nameLength += dataInput.read(name);
                    if (filePathlength == nameLength) {
                        break;
                    }
                }


                filePath = new String(name);
                File file = new File(basePath + filePath);// 得到(盘符+文件名 )绝对路径
                if (!file.exists()) {
                    file.createNewFile();
                }


                //文件内容长度
                long filelength = dataInput.readLong();

                //累加长度
                long sumLength = 0;

                //每次接收多少字节
                byte byt[] = new byte[1024 * 1024 *20];//

                dataOutput = new DataOutputStream(new FileOutputStream(file));

                int len = 0;
                while (true) {
                    if (filelength == sumLength) {// 如果客户端写过来的文件长度等于我们要读取的文件长度就结束循环
                        break;
                    }

                    if (filelength - sumLength < dataInput.available()) {
                        len = dataInput.read(byt, 0, Integer.valueOf(String.valueOf((filelength - sumLength))));
                    } else {
                        if ((filelength - sumLength) > (1024 * 1024 * 20)) {
                            len = dataInput.read(byt);
                        } else {
                            len = dataInput.read(byt, 0, Integer.valueOf(String.valueOf((filelength - sumLength))));
                        }
                    }

                    sumLength += len;
                    dataOutput.write(byt, 0, len);
                    dataOutput.flush();

                }
                System.out.println(filePath + "接收完成了...");
                dataOutput.close();

            }
        }
    }
}
package Nio.NioWork.manyFileupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NioServer {

    private static String basePath = "C:\\Users\\Administrator\\Desktop\\测试";
    public static Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
    public static Map<SelectionKey, Long> fileSumLength = new HashMap<>();
    public static Map<SelectionKey, Long> sum = new HashMap<>();
    private static Selector selector = null;
    ServerSocketChannel  serverSocketChannel = null;

    public static void main(String[] args) throws Exception {
        new NioServer();
        while (true) {
            // 等待请求，每次等待阻塞3s，超过时间则向下执行，若传入0或不传值，则在接收到请求前一直阻塞
            if (selector.select(3000) == 0) {
                System.out.println("等待请求......");
                continue;
            }

            // 获取待处理的选择键集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 如果是连接请求，调用处理器的连接处理方法
                if (selectionKey.isAcceptable()) {/* 判断accept事件 */
                    // 返回创建此键的通道，接受客户端建立连接的请求，并返回 SocketChannel 对象
                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                    //通道连接
                    SocketChannel socketChannel = server.accept();
                    //通道设置为非堵塞
                    socketChannel.configureBlocking(false);
                    //注册可读事件
                    socketChannel.register(selector, SelectionKey.OP_READ);


                } else if (selectionKey.isReadable()) {//判断是否可读事件
                    receiveData(selectionKey);//是可读事件执行接收数据方法
                }
                // 处理完毕从待处理集合移除该选择键
                iterator.remove();
            }
        }
    }

    public NioServer(){

        //开启一个选择器
        try {
            selector = Selector.open();
        //开启一个通道
        serverSocketChannel = ServerSocketChannel.open();
        //设置为非堵塞
        serverSocketChannel.configureBlocking(false);
        //与本地端口绑定
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        // 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取通道中的数据到Object里去
     *
     * @param selectionKey
     * @return
     * @throws IOException
     */
    public static void receiveData(SelectionKey selectionKey) throws IOException {
        // 返回创建此键的通道，接受客户端建立连接的请求，并返回 SocketChannel 对象
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        String path = null;
        String fileName = null;
        // 标识符 判定文件跟文件夹
        //第一次进入
        if (fileMap.get(selectionKey) == null) {
        ByteBuffer buffer0 = ByteBuffer.allocate(4);
        int mark = 0;
        int size = 0;
        while (true) {
            size = socketChannel.read(buffer0);
            if (size >= 4) {
                buffer0.flip();
                mark = buffer0.getInt();
                buffer0.clear();
                break;
            }
        }

        // 文件夹
        if (mark == 1) {
            ByteBuffer buffer5 = ByteBuffer.allocate(4);
            int fileNamelength = 0;

            // 拿到文件夹名的长度
            while (true) {
                size = socketChannel.read(buffer5);
                if (size >= 4) {
                    buffer5.flip();
                    fileNamelength = buffer5.getInt();
                    buffer5.clear();
                    break;
                }
            }

            ByteBuffer buffer6 = ByteBuffer.allocate(fileNamelength);
            byte[] bytes = null;
            while (true) {
                size = socketChannel.read(buffer6);
                if (size >= fileNamelength) {
                    buffer6.flip();
                    bytes = new byte[fileNamelength];
                    buffer6.get(bytes);
                    buffer6.clear();
                    break;
                }

            }
            path = new String(bytes);

            File file = new File(basePath+ File.separator + path);
            if (!file.exists())//判断文件夹是否存在
            {
                file.mkdirs(); //不存在就创建
                System.out.println("文件夹  "+ file.getPath()+ "创建成功");
            }
        }
        // 文件
        else if (mark == 2) {

            //文件名长度
            ByteBuffer buffer1 = ByteBuffer.allocate(4);
            int fileNamelength = 0;
            while (true) {
                size = socketChannel.read(buffer1);
                if (size >= 4) {
                    buffer1.flip();
                    fileNamelength = buffer1.getInt();
                    buffer1.clear();
                    break;
                }
            }
            //文件长度
            ByteBuffer buffer2 = ByteBuffer.allocate(fileNamelength);
            byte[] bytes = null;
            while (true) {
                size = socketChannel.read(buffer2);
                if (size >= fileNamelength) {
                    buffer2.flip();
                    bytes = new byte[fileNamelength];
                    buffer2.get(bytes);
                    buffer2.clear();
                    break;
                }

            }
            path = new String(bytes);

            File file1 = new File(basePath+File.separator + path);
            if (!file1.exists()) {
                File file2 = new File(file1.getParent());
                if (!file2.isDirectory()) {
                    file2.mkdirs();
                }
                file1.createNewFile();
                System.out.println("文件:" + file1.getName()+"接收完成");
            }
            //文件内容长度
            long fileLength = 0;
            ByteBuffer buffer3 = ByteBuffer.allocate(8);
            while (true) {
                size = socketChannel.read(buffer3);
                if (size >= 8) {
                    buffer3.flip();
                    // 文件长度是可要可不要的，如果你要做校验可以留下
                    fileLength = buffer3.getLong();

                    buffer3.clear();

                    break;
                }

            }
            //文件内容
            ByteBuffer buffer4 = ByteBuffer.allocate(1024 * 1024);
            socketChannel.read(buffer4);

            fileName = basePath + File.separator + path;
            FileChannel fileContentChannel = new FileOutputStream(new File(fileName)).getChannel();
            buffer4.flip();
            long fileContentlength = fileContentChannel.write(buffer4);

            buffer4.clear();

            fileContentlength =(sum.get(selectionKey)==null ? 0 : sum.get(selectionKey)) +fileContentlength;

             sum.put(selectionKey,fileContentlength);

            if (sum.get(selectionKey) == fileSumLength.get(selectionKey)) {

                fileContentChannel.close();
                socketChannel.close();

            } else {
                fileMap.put(selectionKey, fileContentChannel);

                fileSumLength.put(selectionKey,fileLength);
            }
            System.out.println("文件" + path + "接收完成");
        }
            } else {
                ByteBuffer buffer4 = null;

                buffer4 =ByteBuffer.allocate(1024 *1024);

                socketChannel.read(buffer4);// 每次读取的长度

                FileChannel fileContentChannel = fileMap.get(selectionKey);
                buffer4.flip();
                long fileContentlength = fileContentChannel.write(buffer4);
                fileContentlength =(sum.get(selectionKey)==null ? 0 : sum.get(selectionKey)) +fileContentlength;

                sum.put(selectionKey,fileContentlength);
                buffer4.clear();


            if (sum.get(selectionKey).longValue()== fileSumLength.get(selectionKey).longValue()) {
                fileContentChannel.close();
              //  fileMap.put(selectionKey,fileContentChannel);
                socketChannel.close();

                }
            }
        }
    }


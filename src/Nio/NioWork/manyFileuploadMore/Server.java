package Nio.NioWork.manyFileuploadMore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server {

    private static String basePath = "C:\\Users\\Administrator\\Desktop\\测试";
    private static ServerSocketChannel  serverSocketChannel = null;
    public static Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
    public static Map<SelectionKey, Long> sumMap = new HashMap<SelectionKey, Long>();
    public static Map<SelectionKey, Long> fileLengthMap = new HashMap<SelectionKey, Long>();
    public static Map<SelectionKey, String> fileNameMap = new HashMap<SelectionKey, String>();


    public static void main(String[] args) throws Exception {
        //开启一个选择器
        Selector selector = Selector.open();
        //开启一个通道
        serverSocketChannel = ServerSocketChannel.open();
        //设置为非堵塞
        serverSocketChannel.configureBlocking(false);
        //与本地端口绑定
        serverSocketChannel.socket().bind(new InetSocketAddress("192.168.1.57", 6666));
        // 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

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
            fileNameMap.put(selectionKey,path);

            //文件内容长度
            ByteBuffer buffer3 = ByteBuffer.allocate(8);
            while (true) {
                size = socketChannel.read(buffer3);
                if (size >= 8) {
                    buffer3.flip();
                    // 文件长度是可要可不要的，如果你要做校验可以留下
                    long fileLength = buffer3.getLong();
                    fileLengthMap.put(selectionKey,fileLength);

                    buffer3.clear();

                    break;
                }

            }
            //文件内容
            sumMap.put(selectionKey,0L);

            ByteBuffer buffer4 = null;

            if (fileLengthMap.get(selectionKey) - sumMap.get(selectionKey) < 1024 * 1024) {
                buffer4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLengthMap.get(selectionKey) - sumMap.get(selectionKey))));

            } else {
                buffer4 = ByteBuffer.allocate(1024 * 1024);
            }
            socketChannel.read(buffer4);
            fileName = basePath + File.separator + path;
            FileChannel fileContentChannel = new FileOutputStream(new File(fileName)).getChannel();
            buffer4.flip();
            long fileContentlength = fileContentChannel.write(buffer4);

            buffer4.clear();

            sumMap.put(selectionKey, sumMap.get(selectionKey) +fileContentlength);

            if (sumMap.get(selectionKey).longValue() == fileLengthMap.get(selectionKey).longValue()) {
                sumMap.put(selectionKey,0L);
                fileLengthMap.put(selectionKey,0L);
                fileMap.put(selectionKey, null);
                fileContentChannel.close();

            } else {
                fileMap.put(selectionKey, fileContentChannel);
            }
            System.out.println("文件" + path + "接收完成");
        }
            } else {
                ByteBuffer buffer4 = null;
                if (fileLengthMap.get(selectionKey) - sumMap.get(selectionKey) < 1024 * 1024) {
                    buffer4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLengthMap.get(selectionKey) - sumMap.get(selectionKey))));

                } else {
                    buffer4 = ByteBuffer.allocate(1024 * 1024);

                }

                socketChannel.read(buffer4);// 每次读取的长度

                FileChannel fileContentChannel = fileMap.get(selectionKey);
                buffer4.flip();
                long fileContentlength = fileContentChannel.write(buffer4);
                sumMap.put(selectionKey,sumMap.get(selectionKey) + fileContentlength);
                buffer4.clear();


                if (sumMap.get(selectionKey).longValue() == fileLengthMap.get(selectionKey).longValue()) {
                    sumMap.put(selectionKey,0L);
                    fileLengthMap.put(selectionKey,0L);
                    fileMap.put(selectionKey, null);

                    fileContentChannel.close();
                }
            }
        }
    }


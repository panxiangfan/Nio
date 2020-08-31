package Netty.file2;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;  
  
public class NioserverHandler {  
      
    private final static Logger logger = Logger.getLogger(NioserverHandler.class.getName());
    private final static String DIRECTORY = "C:\\Users\\Administrator\\Desktop\\测试\\";
      
    /** 
     * 这里边我们处理接收和发送 
     *  
     * @param serverSocketChannel 
     */  
    public void excute(ServerSocketChannel serverSocketChannel) {  
        SocketChannel socketChannel = null;  
        try {  
            socketChannel = serverSocketChannel.accept(); // 等待客户端连接  
              
            RequestObject requestObject = receiveData(socketChannel);// 接数据   
            logger.log(Level.INFO,requestObject.toString());  
            writeFile(DIRECTORY,requestObject); // 写文件  
            String response = "File " + new String(requestObject.getFileName()) + " SUCCESS......";  
            responseData(socketChannel, response);  
            logger.log(Level.INFO, response);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * 读取通道中的数据到Object里去 
     * @param socketChannel 
     * @return 
     * @throws IOException 
     */  
    public RequestObject receiveData(SocketChannel socketChannel) throws IOException {  
          
        // 文件名长度  
        int nameLength = 0;  
        // 文件名  
        byte[] fileName = null;   // 如果文件以字节数组发来，我们就无从得知文件名，所以文件名一起发过来
        // 文件长度   
        int contentLength = 0;  
        // 文件内容   
        byte[] contents = null;  
        // 由于我们解析时前4个字节是文件名长度  
        int capacity = 4;  
        ByteBuffer buf = ByteBuffer.allocate(capacity);  
          
        int size = 0;  
        byte[] bytes = null;  
        // 拿到文件名的长度  
        size = socketChannel.read(buf);  
        if (size >= 0) {  
            buf.flip();  
            capacity = buf.getInt();  
            buf.clear();  
            nameLength = capacity;  
        }  
          
        // 拿文件名,相信文件名一次能够读完,如果你文件名超过1k
        buf = ByteBuffer.allocate(capacity);
        size = socketChannel.read(buf);  
        if (size >= 0) {  
            buf.flip();  
            bytes = new byte[size];  
            buf.get(bytes);  
            fileName = bytes;  
            buf.clear();  
        }  
          
        // 拿到文件长度  
        capacity = 4;  
        buf = ByteBuffer.allocate(capacity);  
        size = socketChannel.read(buf);  
        if (size >= 0) {  
            buf.flip();  
            // 文件长度是可要可不要的，如果你要做校验可以留下  
            contentLength = buf.getInt();  
            buf.clear();  
        }  
          
        // 用于接收buffer中的字节数组  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        // 文件可能会很大  
        capacity = 1024;  
        buf = ByteBuffer.allocate(capacity);  
        while((size = socketChannel.read(buf)) >= 0){  
            buf.flip();  
            bytes = new byte[size];  
            buf.get(bytes);  
            baos.write(bytes);  
            buf.clear();  
        }  
        contents = baos.toByteArray();  
        RequestObject requestObject = new RequestObject(nameLength, fileName, contentLength, contents);  
          
        return requestObject;  
    }  
      
    /** 
     * 把接收的数据写到本地文件里 
     * @param drc 本地文件目录 
     * @param requestObject 
     * @throws UnsupportedEncodingException  
     * @throws IOException  
     */  
    public static void writeFile(String drc,RequestObject requestObject) throws UnsupportedEncodingException {  
        File file = new File(drc + File.separator + new String(requestObject.getFileName(),"utf-8"));  
        FileOutputStream os = null;  
        try {  
            os = new FileOutputStream(file);  
            os.write(requestObject.getContents());  
            os.flush();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (os != null)  
                    os.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
      
    /** 
     * 我们接受数据成功后要给客户端一个反馈 
     * 如果是java的客户端我们可以直接给对象,如果不是最好还是给文本或字节数组 
     * @param socketChannel 
     * @param response 
     */  
    private void responseData(SocketChannel socketChannel, String response) {  
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());  
        try {  
            socketChannel.write(buffer);  
            buffer.clear();  
            // 确认要发送的东西发送完了关闭output 不然它端接收时socketChannel.read(Buffer)  
            // 很可能造成阻塞 ，可以把这个（L）注释掉，会发现客户端一直等待接收数据  
            socketChannel.socket().shutdownOutput();  // （L）  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
}  
package file2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;  
  
public class NioserverHandler {  
      
    private final static Logger logger = Logger.getLogger(NioserverHandler.class.getName());  
    private final static String DIRECTORY = "C:\\Users\\Administrator\\Desktop\\测试";
      
    /** 
     * 这里边我们处理接收和发送 
     *  
     * @param s
     */  
    public void excute(SelectionKey s) {  
        try {  
        	SocketChannel server = ((ServerSocketChannel) s.channel()).accept();
            receiveData(server,s);// 接数据   
           
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
    
    public static Map<SelectionKey, Integer> fileMap = new HashMap<SelectionKey, Integer>();
    public static String fileName = null;
    
    public void receiveData(SocketChannel socketChannel,SelectionKey s) throws IOException {
    	
    	
    	if(fileMap.get(s) == null)
    	{
	    	 ByteBuffer buf1 = ByteBuffer.allocate(4);
	    	 int size = 0;  
	    	 int fileNamelength = 0;
	         // 拿到文件名的长度  
	         size = socketChannel.read(buf1);  
	         if (size >= 0) {  
	             buf1.flip();  
	             fileNamelength = buf1.getInt();  
	             buf1.clear();  
	         }  
	         
	         
	         byte[] bytes = null;
	         ByteBuffer buf2 = ByteBuffer.allocate(fileNamelength);
	         size = socketChannel.read(buf2);  
	         if (size >= 0) {  
	        	 buf2.flip();  
	        	  bytes = new byte[fileNamelength];  
	             buf2.get(bytes);  
	             buf2.clear();  
	         }  
	         
	         fileName = new String(bytes);
	         
	         
	         
	         long fileLengh = 0;
	         ByteBuffer buf3 = ByteBuffer.allocate(8);  
	         size = socketChannel.read(buf3);  
	         if (size >= 0) {  
	             buf3.flip();  
	             // 文件长度是可要可不要的，如果你要做校验可以留下  
	             fileLengh = buf3.getLong();  
	             
	             buf3.clear();  
	         }  
	         
	         
	         
	         
	         
	         ByteBuffer buf4 = ByteBuffer.allocate(1024 * 1024);
				socketChannel.read(buf4);
				String path = DIRECTORY + File.separator + fileName;
				FileChannel fileContentChannel = new FileOutputStream(new File(path)).getChannel();
				buf4.flip();
				fileContentChannel.write(buf4);
				buf4.clear();
				
				fileMap.put(s, 1);
    	}
    	else
    	{
    		ByteBuffer buf4 = ByteBuffer.allocate(1024 * 1024);
			socketChannel.read(buf4);
			String path = DIRECTORY + File.separator + fileName;
			FileChannel fileContentChannel = new FileOutputStream(new File(path)).getChannel();
			buf4.flip();
			fileContentChannel.write(buf4);
			buf4.clear();
    	}
       
    }  
      
  
      
}  
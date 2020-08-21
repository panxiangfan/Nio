package Nio.sample;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
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
            receiveData(s);// 接数据   
           
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
    
    public static Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
    public static Map<SelectionKey, Long> fileSumLength = new HashMap<SelectionKey, Long>();
    public static Map<SelectionKey, Long> sum = new HashMap<SelectionKey, Long>();
    
    public static String fileName = null;
    
    public void receiveData(SelectionKey s) throws IOException {
    	
    	SocketChannel socketChannel = (SocketChannel) s.channel();
    	
    	if(fileMap.get(s) == null)
    	{
    		
    		
	    	 ByteBuffer buf1 = ByteBuffer.allocate(4);
	    	 int size = 0;  
	    	 int fileNamelength = 0;
	         // 拿到文件名的长度  
	    	 while(true)
	    	 {
		         size = socketChannel.read(buf1);  
		         if (size >= 4) {  
		             buf1.flip();  
		             fileNamelength = buf1.getInt();  
		             buf1.clear();  
		             break;
		         }
	    	 }
	         
	         
	         byte[] bytes = null;
	         ByteBuffer buf2 = ByteBuffer.allocate(fileNamelength);
	         while(true)
	         {
		         size = socketChannel.read(buf2);  
		         if (size >= fileNamelength) {  
		        	 buf2.flip();  
		        	 bytes = new byte[fileNamelength];  
		             buf2.get(bytes);  
		             buf2.clear();
		             break;
		         }  
		         
	         }
	         fileName = new String(bytes);
	         
	         
	         
	         
	         
	         long fileLengh = 0;
	         ByteBuffer buf3 = ByteBuffer.allocate(8);
	         while(true)
	         {
	        	 size = socketChannel.read(buf3); 
		         if (size >= 8) {  
		             buf3.flip();  
		             // 文件长度是可要可不要的，如果你要做校验可以留下  
		             fileLengh = buf3.getLong();  
		             
		             buf3.clear();  
		             
		             break;
		         }
		         
	         }
	         
	         
	         
	         	ByteBuffer buf4 = ByteBuffer.allocate(1024 * 1024);
				socketChannel.read(buf4);
				String path = DIRECTORY + File.separator + fileName;
				FileChannel fileContentChannel = new FileOutputStream(new File(path)).getChannel();
				buf4.flip();
				long a = fileContentChannel.write(buf4);
				buf4.clear();
				
				a = (sum.get(s) == null ? 0 :  sum.get(s))  + a;
				sum.put(s, a);
				
				if(sum.get(s) == fileSumLength.get(s))
				{
					fileContentChannel.close();
					socketChannel.close();
				}
				
				
				fileMap.put(s, fileContentChannel);
				fileSumLength.put(s, fileLengh);
    	}
    	else
    	{
    		ByteBuffer buf4 = ByteBuffer.allocate(1024 * 1024 );
			socketChannel.read(buf4);//每次读取的长度
			
			
			
		
			//String path = DIRECTORY + File.separator + fileName;
			//FileChannel fileContentChannel = new FileOutputStream(new File(path)).getChannel();
			FileChannel fileContentChannel = fileMap.get(s);
			buf4.flip();
			long a =fileContentChannel.write(buf4);
			
			a = (sum.get(s) == null ? 0 :  sum.get(s))  + a;
			sum.put(s, a);
			
			buf4.clear();
			
			
			if(sum.get(s).longValue() == fileSumLength.get(s).longValue())
			{
				fileContentChannel.close();
				socketChannel.close();
			}
    	}
       
    }  
      
  
      
}  
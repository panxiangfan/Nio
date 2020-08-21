package Nio.file;
  
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ReceiveAndSend {  
  
    /** 
     * 接收文件 
     * @param sc 
     * @throws IOException
     * aaa.txt|10|;;;;;;;1231231231bbb.txt 
     */  
    public void receiveFile(SocketChannel sc) throws IOException{  
       
        
        //设置接收消息头缓冲区  
        ByteBuffer headByte=ByteBuffer.allocateDirect(32);  
        //接收消息头  
        sc.read(headByte);  
        byte[] b=new byte[32];  
        headByte.flip();  
          
        for (int i = 0; headByte.hasRemaining(); i++) {  
            b[i]=headByte.get();  
        }  
        headByte.clear();  
        //获取文件信息  
        String fileInfo=new String(b,Charset.forName("UTF-8"));  
        String[] strInfo=fileInfo.split("\\|");  
        System.out.println("文件："+strInfo[0]+"--大小："+strInfo[1]); 
        
        //获取保存文件  
        File file=new File("f:"+File.separator+strInfo[0]);  
        FileOutputStream fos=new FileOutputStream(file);  
        //获取通道  
        FileChannel foc = fos.getChannel();  
        int lenght = Integer.valueOf(strInfo[1]);
        
        
        //设置接收消息体缓冲区  
        ByteBuffer bb=ByteBuffer.allocateDirect(1024);
        if(lenght < 1024)
        {
        	bb=ByteBuffer.allocateDirect(lenght);
        }
        
        int read=sc.read(bb);  
          
        //1500
        // 1024   476
        int sum = 0;
        boolean mark = false;
        while(read!=-1){  
            bb.flip();  
            //写入到输出通道  
            sum = sum +foc.write(bb);  
            if((lenght - sum) < 1024)
            {
            	bb=ByteBuffer.allocateDirect(lenght - sum);
            	 bb.clear();  
                 read=sc.read(bb);
                 bb.flip();
                 sum = sum +foc.write(bb); 
            	mark = true;
            }
            if(mark)
            {
            	receiveFile(sc);
            }else
            {
            	 bb.clear();  
                 read=sc.read(bb);
            }
            
        
        }  
        foc.close();  
        fos.close();  
    }  
    public static void main(String[] args) {
    	String head="aa"+"|"+"bb"+"|;";  
    	System.out.println(head);
	}
    
    public  String decode(ByteBuffer buffer)
      {
              System.out.println( " buffer= "   +  buffer);
               Charset charset  =   null ;
               CharsetDecoder decoder  =   null ;
               CharBuffer charBuffer  =   null ;
                try 
           {
                   charset  =  Charset.forName("UTF-8");
                  decoder  =  charset.newDecoder();
                  charBuffer  =  decoder.decode(buffer);
                  System.out.println( " charBuffer= "   +  charBuffer);
                 System.out.println(charBuffer.toString());
                   return  charBuffer.toString();
              } 
              catch  (Exception ex)
           {
                  ex.printStackTrace();
                   return   "" ;
              } 
       }
    /** 
     * 发送文件 
     * @param sc 
     * @param path 
     * @param fileName 
     * @throws IOException 
     */  
    public void sendFile(SocketChannel sc,String path,String fileName) throws IOException{  
        File file=new File(path);  
        FileInputStream fis = new FileInputStream(file);  
          
        FileChannel fic = fis.getChannel();  
          
        ByteBuffer bb = ByteBuffer.allocateDirect(1024);  
        ByteBuffer headbb = ByteBuffer.allocateDirect(32);  
        int read=0;  
        long fileSize = file.length();  
        long sendSize=0;  
        System.out.println("文件大小："+fileSize);  

        String head=fileName+"|"+fileSize+"|;";  
        //将头信息写入缓冲区  
        //把字符串放入ByteBuffer中，可以让socket通道进入传输
        headbb.put(head.getBytes(Charset.forName("UTF-8")));  
        int c=headbb.capacity()-headbb.position();  
        //填满头信息  
        for (int i = 0; i < c; i++) {  
            headbb.put(";".getBytes(Charset.forName("UTF-8")));  
        }  
        System.out.println(decode(headbb));
        headbb.flip();  
        //将头信息写入到通道  
        sc.write(headbb);  
        do{  
            //将文件写入到缓冲区  
            read = fic.read(bb);  
            sendSize+=read;  
            bb.flip();  
            //将文件写入到通道  
            sc.write(bb);  
            bb.clear();  
            System.out.println("已传输/总大小："+sendSize+"/"+fileSize);  
        }while(read!=-1&&sendSize<fileSize);  
        System.out.println("文件传输成功");  
        fic.close();  
        fis.close();  
    }  
}  
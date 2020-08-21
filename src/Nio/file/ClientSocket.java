package Nio.file;
  
import java.io.File;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientSocket {  
  
    public static void main(String[] args) throws Exception {  
          
        int port=8000;  
        String ip = "localhost";  
        //打开传输通道  
        SocketChannel sc = SocketChannel.open();  
        //连接  
        sc.connect(new InetSocketAddress(ip,port));  
        //发送文件  
        for(int i = 0 ;i<3;i++){
        	if(i == 0)
        	{
        		new ReceiveAndSend().sendFile(sc, "D:"+File.separator+"tips.txt","tips.txt");
        	}
        	else if(i == 1)
        	{
        		new ReceiveAndSend().sendFile(sc, "D:"+File.separator+"InletexEMC.exe","InletexEMC.exe");
        	}
        	else
        	{
        		new ReceiveAndSend().sendFile(sc, "D:"+File.separator+"PCQQ2019.exe","PCQQ2019.exe");
        	}
        }
        //关闭传输通道  
        sc.close();  
          
    }  
}  
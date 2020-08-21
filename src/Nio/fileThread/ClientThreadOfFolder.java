package Nio.fileThread;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientThreadOfFolder extends Thread{
	private String path = null;
	private File file = null;
	
	public ClientThreadOfFolder(File file,String path) {
		this.file = file;
		this.path = path;
	}
	
	public void run() {
		
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("192.168.1.57",6666));
		
			/* 寰楀埌鏂囦欢鍚? */
			String filePath =  file.getPath();
			filePath = filePath.replace(path, "");
			
			/* 鍙戦?佹枃浠跺悕闀垮害銆佹枃浠跺悕銆佹爣璇? */
			ByteBuffer fileNameAndTypeBuffer = ByteBuffer.allocate(4 + new String(filePath.getBytes(),"ISO-8859-1").length() + 4);
			fileNameAndTypeBuffer.putInt(new String(filePath.getBytes(),"ISO-8859-1").length());//鏂囦欢鍚嶉暱搴?
			fileNameAndTypeBuffer.put(filePath.getBytes());//鏂囦欢鍚?
			fileNameAndTypeBuffer.putInt(1);//鏍囪瘑鏂囦欢
			fileNameAndTypeBuffer.flip();
			socketChannel.write(fileNameAndTypeBuffer);
			fileNameAndTypeBuffer.clear();//鎶婂綋鍓嶄綅缃疆涓?0	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
                if (socketChannel != null){
                	System.out.println("鍏抽棴");
                	socketChannel.close();
                    Client.number--;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
	}
}

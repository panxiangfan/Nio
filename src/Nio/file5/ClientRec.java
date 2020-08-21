package Nio.file5;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ClientRec {

	private static String basePath = "E:\\合照";
	public static SocketChannel socketChannel = null;	
	public static SocketAddress socketAddress = null;
	
	public static void main(String[] args) throws Exception {
		
		
		
		socketChannel = SocketChannel.open();
		socketAddress = new InetSocketAddress("192.168.1.57", 8888);
		socketChannel.connect(socketAddress);
		
		
		ClientRec.sendFile(basePath);
		//System.out.println(dataOutput);
		Thread.sleep( 10 * 100000);
		//如果服务端接收成功，返回一个消息．告诉客户端

	}

	public static void sendFile(String filePath) throws Exception {

		File file = new File(filePath);
		if (file.isDirectory())// 判断是否为文件夹
		{
			//不发送最基础目录
			if (!filePath.equals(basePath)) {
				
			}
			//递归
			String files []= file.list();
			for (String fPath : files) {
				sendFile(filePath+File.separator + fPath);
			}
		} else {
			
			String rfilepath = filePath.replace(basePath + "\\", "");


			ByteBuffer buffer0 = ByteBuffer.allocate(4);
			buffer0.putInt(2);

			buffer0.flip();
			socketChannel.write(buffer0);// 发送
			buffer0.clear();

			// 文件名长度
			ByteBuffer buffer1 = ByteBuffer.allocate(4);
			buffer1.putInt(new String(rfilepath.getBytes(), "ISO-8859-1").length());

			buffer1.flip();
			socketChannel.write(buffer1);// 发送
			buffer1.clear();

			ByteBuffer buffer2 = ByteBuffer.allocate(new String(rfilepath.getBytes(), "ISO-8859-1").length());
			buffer2.put(rfilepath.getBytes());
			buffer2.flip();
			socketChannel.write(buffer2);// 发送
			buffer2.clear();

			ByteBuffer buffer3 = ByteBuffer.allocate(8);
			long fileContentLength = new File(filePath).length();
			buffer3.putLong(fileContentLength);
			buffer3.flip();
			socketChannel.write(buffer3);// 发送
			buffer3.clear();

			ByteBuffer buffer4 = ByteBuffer.allocate(1024 * 1024);
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			FileChannel fileChannel = fileInputStream.getChannel();
			long nowReadLength = 0;// 每次读取的文件内容长度
			long sumReadLength = 0;// 累加长度
			do {
				nowReadLength = fileChannel.read(buffer4);
				sumReadLength += nowReadLength;
				buffer4.flip();
				socketChannel.write(buffer4);
				System.out.println(filePath+"发送完成了...");
				buffer4.clear();
			} while (nowReadLength != -1 && sumReadLength < fileContentLength);
		}
	}
}

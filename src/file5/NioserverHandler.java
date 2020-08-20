package file5;

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
	 * @param
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
	 * 
	 * @param socketChannel
	 * @return
	 * @throws IOException
	 */

	public static Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
	private static long sum =0;
	private static long fileLength = 0;

	public static String fileName = null;

	public void receiveData(SelectionKey s) throws IOException {

		SocketChannel socketChannel = (SocketChannel) s.channel();

		if (fileMap.get(s) == null) {

				ByteBuffer buf1 = ByteBuffer.allocate(4);
				int fileNamelength = 0;
				int size = 0;
				// 拿到文件名的长度
				while (true) {
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
				while (true) {
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

				ByteBuffer buf3 = ByteBuffer.allocate(8);
				while (true) {
					size = socketChannel.read(buf3);
					if (size >= 8) {
						buf3.flip();
						// 文件长度是可要可不要的，如果你要做校验可以留下
						fileLength = buf3.getLong();

						buf3.clear();

						break;
					}

				}

				
				ByteBuffer buf4 = null;
				if(fileLength - sum < 1024*1024)
				{
					buf4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLength - sum)));
					
				}
				else
				{
					buf4 = ByteBuffer.allocate(1024 * 1024);

				}
				socketChannel.read(buf4);
				String path = DIRECTORY + File.separator + fileName;
				FileChannel fileContentChannel = new FileOutputStream(new File(path)).getChannel();
				buf4.flip();
				long a = fileContentChannel.write(buf4);
				buf4.clear();

				sum = sum + a;

				if (sum== fileLength) {
					sum = 0;
					fileLength = 0;
					fileMap.put(s, null);
					fileContentChannel.close();
				}
				else
				{
					fileMap.put(s, fileContentChannel);
				}

		} else {
			ByteBuffer buf4 = null;
			if(fileLength - sum < 1024*1024)
			{
				buf4 = ByteBuffer.allocate(Integer.valueOf(String.valueOf(fileLength - sum)));
				
			}
			else
			{
				buf4 = ByteBuffer.allocate(1024 * 1024);

			}
			
			socketChannel.read(buf4);// 每次读取的长度

			// String path = DIRECTORY + File.separator + fileName;
			// FileChannel fileContentChannel = new FileOutputStream(new
			// File(path)).getChannel();
			FileChannel fileContentChannel = fileMap.get(s);
			buf4.flip();
			long a = fileContentChannel.write(buf4);
			sum = sum + a;
			buf4.clear();

			
			if (sum== fileLength) {
				sum = 0;
				fileLength = 0;
				fileMap.put(s, null);

				System.out.println(s + "接收完成了...");
				fileContentChannel.close();
			}
			

		}

	}

}
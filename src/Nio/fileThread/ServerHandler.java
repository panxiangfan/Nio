package Nio.fileThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ServerHandler {
	private SelectionKey selectionKey = null;
	private String path = null;
	//key == outputStream(输出流)
	Map<SelectionKey, FileChannel> fileMap = new HashMap<SelectionKey, FileChannel>();
	
	public void test(SelectionKey selectionKey,String path) {
		this.selectionKey = selectionKey;
		this.path = path;
		try {
			receviceFolder(selectionKey,path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void receviceFolder(SelectionKey selectionKey ,String path) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		//1,2,3,4,5,6,7,8,9,10
		if (fileMap.get(selectionKey) == null) {//绗竴娆¤繘
			ByteBuffer fileNameLengthBuffer = ByteBuffer.allocate(4);
			int fileNameLength = 0;//鏂囦欢鍚嶅?肩殑闀垮害
			String filePath = "";
			while (true) {
				int readFileNameLength = socketChannel.read(fileNameLengthBuffer);//鎶婇?氶亾閲岀殑鏂囦欢鍚嶇殑闀垮害瀛楄妭澶у皬璇诲埌缂撳啿鍖哄幓
				if (readFileNameLength == 4) {//鍒ゆ柇鏄惁鎺ユ敹瀹屾枃浠跺悕闀垮害
					break;
				}
			}
			fileNameLengthBuffer.flip();
			fileNameLength = fileNameLengthBuffer.getInt();//浠庣紦鍐插尯浠庤幏鍙栨枃浠跺悕鐨勯暱搴?
			fileNameLengthBuffer.clear();//褰撳墠浣嶇疆缃负0
			
			/* 鎺ユ敹鏂囦欢鍚? */
			int sumReadFileNameLength = 0;//鐢ㄦ潵瀛樿鍙栫殑鏂囦欢鍚嶅?肩殑闀垮害
			ByteBuffer fileNameBuffer = ByteBuffer.allocate(fileNameLength);
			while (true) {
				sumReadFileNameLength = socketChannel.read(fileNameBuffer);//灏嗛?氶亾閲岀殑(鏂囦欢鍚?)鍐呭璇诲埌缂撳啿鍖轰腑
				if (sumReadFileNameLength == fileNameLength) {//濡傛灉宸茶闀垮害==鍚嶅瓧鎬婚暱搴?
					break;
				}
			}
			fileNameBuffer.flip();
			byte[] bFileName = new byte[sumReadFileNameLength];
			fileNameBuffer.get(bFileName);
			fileNameBuffer.clear();
			String nowPath = new String(bFileName);//寰楀埌鏂囦欢鍚?
			
			/* 鏂拌矾寰? */
			filePath = path + nowPath;
			File file = new File(filePath);//寰楀埌鏂拌矾寰?
			
			/* 鎺ユ敹鏍囪瘑 */
			ByteBuffer fileTypeBuffer = ByteBuffer.allocate(4);//鍒涘缓缂撳啿娴侊紝瀹归噺涓?4锛岀敤鏉ユ帴鏀秈nt绫诲瀷鏍囪瘑
			int fileType = 0;
			while (true) {
				int readFileTypeLength = socketChannel.read(fileTypeBuffer);
				if (readFileTypeLength == 4) {
					break;
				}
			}
			fileTypeBuffer.flip();
			fileType = fileTypeBuffer.getInt();
			fileTypeBuffer.clear();
			
			if (fileType == 0) {//鏂囦欢
				System.out.println("鏂囦欢锛?"+filePath);
				if (!file.exists()) {
	                if(!file.getParentFile().exists())
	                {
	                    file.getParentFile().mkdirs();
	                }
	                file.createNewFile();
	            }
				/* 鎺ユ敹鏂囦欢鍐呭闀垮害 */
				ByteBuffer fileContentLengthBuffer = ByteBuffer.allocate(8);//
				long fileContentLength = 0;
				while (true) {
					long readFileContentLength = socketChannel.read(fileContentLengthBuffer);
					if (readFileContentLength == 8) {
						break;
					}
				}
				fileContentLengthBuffer.flip();
				fileContentLength = fileContentLengthBuffer.getLong();
				fileContentLengthBuffer.clear();
				
				/* 鎺ユ敹鏂囦欢鍐呭 */
				FileOutputStream fileOutputStream = new FileOutputStream(file);//鑾峰彇涓?涓緭鍑烘祦
				FileChannel fileContentChannel = fileOutputStream.getChannel();//寰楀埌涓?涓枃浠堕?氶亾
				ByteBuffer fileContentBuffer = ByteBuffer.allocate(1024*1024*5);
				socketChannel.read(fileContentBuffer);
				fileContentBuffer.flip();
				fileContentChannel.write(fileContentBuffer);
				fileContentBuffer.clear();
				
				fileMap.put(selectionKey, fileContentChannel);
			} else {//鏂囦欢澶?
				System.out.println("鏂囦欢澶癸細"+filePath);
				if (!file.exists()) {
					file.mkdirs();
	            }
				System.out.println("鎺ユ敹鎴愬姛锛?"+filePath);
				selectionKey.cancel();
			}
			
		} else {//闈炵涓?娆¤繘
			FileChannel fileContentChannel = fileMap.get(selectionKey);//寰楀埌涓?涓枃浠堕?氶亾
			ByteBuffer fileContentBuffer = ByteBuffer.allocate(1024*1024*10);
			socketChannel.read(fileContentBuffer);
			fileContentBuffer.flip();
			fileContentChannel.write(fileContentBuffer);
			fileContentBuffer.clear();
		}
		
	}
	
}

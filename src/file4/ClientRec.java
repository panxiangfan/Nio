package file4;

import java.io.File;

public class ClientRec {

	private static String basePath = "E:\\服务器";

	public static void main(String[] args) throws Exception {
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
				NioClientHandlerFolder nioClientHandlerFolder = new NioClientHandlerFolder(filePath,basePath);
				nioClientHandlerFolder.start();
				nioClientHandlerFolder.join();
			}
			//递归
			String files []= file.list();
			for (String fPath : files) {
				sendFile(filePath+File.separator + fPath);
			}
		} else {
			new NioClientHandlerFile(filePath,basePath).start();
		}
	}
}

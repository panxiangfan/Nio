package test3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
 
/**
* @类名  Server.java
* @描述    NIO提供了selector【选择器】来代替List<SocketChannel>,具有自动检测通道中有无内容机制
*
*/
public class Server {

	public static Charset charset = Charset.forName("UTF-8");
	public static List<SocketChannel> list = new ArrayList<>();
	public static ServerSocketChannel ssc = null;

	public static void main(String[] args) {
		try {
			ssc = ServerSocketChannel.open();//获取通道
			ssc.configureBlocking(false);//非阻塞
			SocketAddress local = new InetSocketAddress(7777);//套接字服务端端口
			ssc.bind(local);

//			read() ;//读取文字信息

			readFile();//读取客户端传输过来的文件【字节流】

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reddd(SocketChannel soc, long len, int capacity, FileOutputStream fos, FileChannel fch) throws Exception {
		ByteBuffer bb = ByteBuffer.allocate(capacity);
		int read;
		while ((read = soc.read(bb)) > 0) {
//			System.out.println(read);
			bb.flip();
			while (bb.hasRemaining()) {
				fch.write(bb);
			}
			fos.flush();
			Thread.sleep(1);//休眠一毫秒，这是关键代码，不可省略【应该有可替代方案】,休眠时间长短与客户端逻辑处理有关
			bb.clear();

			len -= read;
			if (len <= 0) {
//				System.out.println(len);
				return;//结束当前方法
			}
			if (len < capacity) {//最后一次读取，数据少于容量值，按剩余长度再读取一次
				reddd(soc, len, (int) len, fos, fch);
				return;//结束当前方法
			}
		}
	}

	//接受文件
	public static void readFile() throws Exception {

		while (true) {
			SocketChannel ac = ssc.accept();
			if (ac != null) {
				ac.configureBlocking(false);
				list.add(ac);
				System.out.println(ac);
			}
			for (SocketChannel soc : list) {
				FileOutputStream fos = null;
				FileChannel fch = null;

				/*****获取文件大小 *******/
				ByteBuffer cc = ByteBuffer.allocateDirect(8);
				long len = 0;
				if (soc.read(cc) > 0) {
					long time = System.currentTimeMillis();//用时间戳命名【系统处理速度很快，时间戳命名，有可能造成文件覆盖，即不足一毫秒就处理完成】
					fos = new FileOutputStream("D://text/img/" + (time) + ".jpg");
					fch = fos.getChannel();
					cc.flip();
					len = cc.getLong();
					System.err.println(len);
					cc.clear();
				}
				;
				/*****获取文件大小    *******/

				/*****获取文件流 *******/
				reddd(soc, len, 4096, fos, fch);

			}
		}

	}


	//读信息
	public static void read() throws IOException {
		ByteBuffer bb = ByteBuffer.allocateDirect(1024);
		while (true) {
			int len = 0;
			SocketChannel ac = ssc.accept();
			if (ac != null) {
				list.add(ac);
				System.out.println(ac);
			}
			for (SocketChannel soc : list) {
				int read = 0;
				while ((read = soc.read(bb)) > 0) {
					bb.flip();
					System.out.println(charset.decode(bb));
					bb.clear();

					//回写信息
					soc.write(charset.encode("收到了，mv"));
				}
			}
		}
	}
}

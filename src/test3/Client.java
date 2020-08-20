package test3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
 
/**
* @作者  jinbanglong
* @描述      支持多个客户端启动
*
*/
public class Client {
	
	public static Charset charset = Charset.forName("UTF-8");
	public static SocketChannel sc=null;
	public static void main(String[] args) throws IOException{
		SocketAddress remote=new InetSocketAddress("127.0.0.1", 7777);
		Scanner scc = new Scanner(System.in);
		try {
			sc = SocketChannel.open(remote);
			sc.configureBlocking(false);
			
			/********读消息线程********/
//			new Thread(new Read()).start();
			/********写消息方法【主线程】********/
//			write(scc);
			
			/***    循环调用文件发送【主线程】     ********/
			/***    数据格式： D:/text/2.jpg,D:/text/3.jpg     **/
			while(true) {
				String[] path=scc.nextLine().split(",");//一次输入多个文件路径
				for(String p:path) {
					upload(p);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			scc.close();
			if(sc!=null) {
				sc.close();
			}
		}
	}
	
	//写消息操作
	public static void write(Scanner scc) throws Exception {
		while(scc.hasNextLine()) {
			String nline = scc.nextLine();
			sc.write(charset.encode(nline));
		}
	}
	/**
	 * @desc 多文件传输
	 * @desc 可以在write方法中根据特定命令调用 
	 **/
	public static void upload(String path) throws Exception {
		FileChannel fc=null;
		FileInputStream fis=null;
		ByteBuffer bb=ByteBuffer.allocateDirect(4096);
		try {
			fis = new FileInputStream(path);
			fc = fis.getChannel();
			
			/****************向服务端写入文件大小 *********************/
			long size = fc.size();
			ByteBuffer cc=ByteBuffer.allocateDirect(8);//long 容量不得小于8
			cc.putLong(size);
			cc.flip();
			sc.write(cc);
			cc.clear();
			/****************向服务端写入文件大小*完成 *********************/

			/****************向服务端写入文件内容 *********************/
			int len=-1;
			while((len=fc.read(bb))!=-1) {
				bb.flip();
				while(bb.hasRemaining()) {//保证字节全部写入
					sc.write(bb);
				}
				bb.clear();
			}
			fc.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//读数据  类
	static class Read implements Runnable{
		@Override
		public void run() {
			while(sc!=null) {
				int read =0 ;
				ByteBuffer bb = ByteBuffer.allocateDirect(1024);
				try {
					while((read=sc.read(bb))>0) {
						bb.flip();
						System.out.println(charset.decode(bb));
						bb.clear();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
 
	
}

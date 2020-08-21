package Nio.fileThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
	private  Selector selector;
	public static String path = "C:\\Users\\Administrator\\Desktop\\测试";
	
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.initServer(6666);//鍒涘缓閫氶亾
		server.listen();//鍙戦?佷俊鎭?佹帴鏀朵俊鎭?
	}
	
	public void initServer(int port) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//鍒涘缓寮?鍚湇鍔＄閫氶亾
		serverSocketChannel.configureBlocking(false);//閰嶇疆闈為樆濉炴ā寮?
		serverSocketChannel.socket().bind(new InetSocketAddress(port));//缁戝畾鐩戝惉绔彛
		this.selector = Selector.open();//寰楀埌骞跺紑鍚?夋嫨鍣?
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//灏嗘湇鍔＄閫氶亾娉ㄥ唽鍒伴?夋嫨鍣紝骞舵敞鍐宎ccept浜嬩欢
	}
	ServerHandler serverHandler = new ServerHandler();
	
	@SuppressWarnings("unchecked")//蹇界暐璀﹀憡淇℃伅
	public void listen() throws IOException {
		System.out.println("鏈嶅姟绔惎鍔ㄦ垚鍔?...");
		while (true) {
			int readyChannelsNum = selector.select();//鑾峰彇閫夋嫨鍣ㄤ腑宸插氨缁殑閫氶亾鐨勪釜鏁?
			if (readyChannelsNum == 0) {//濡傛灉娌℃湁涓?涓?氶亾鏄氨缁殑锛屽氨绛夊緟
				System.out.println("绛夊緟杩炴帴...");
				continue;
			}
			
			Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();//杩唬
			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				
				if (selectionKey.isAcceptable()) {/* 鍒ゆ柇accept浜嬩欢 */
					System.out.println("1111111111111111111");
					ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel(); 
					SocketChannel socketChannel = server.accept();
					socketChannel.configureBlocking(false);
					socketChannel.register(this.selector, SelectionKey.OP_READ);
				} else if (selectionKey.isReadable()) {//鍒ゆ柇鏄惁鍙浜嬩欢
					 serverHandler.test(selectionKey, path);
				}
				
				iterator.remove();
				
			}
		}
	}
		
}

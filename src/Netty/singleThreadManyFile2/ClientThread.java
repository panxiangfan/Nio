package Netty.singleThreadManyFile2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ClientThread extends Thread {
	
	public String filePath;
	private int mark;
	
	public ClientThread(String filePath,int mark) {
		this.mark = mark;
		this.filePath = filePath;
	}
	
	static final boolean SSL = System.getProperty("ssl") != null;
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
	static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
	
	
	@Override
	public void run() {
		try {
			final SslContext sslCtx;
			if (SSL) {
				sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
			} else {
				sslCtx = null;
			}

			EventLoopGroup group = new NioEventLoopGroup();
			try {
				Bootstrap b = new Bootstrap();
				b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							public void initChannel(SocketChannel ch) throws Exception {
								ChannelPipeline p = ch.pipeline();
								if (sslCtx != null) {
									p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
								}
								p.addLast(new Encoder(),new ByteArrayEncoder(), new ChunkedWriteHandler(),new StringDecoder(),new EchoClientHandler(filePath,mark));
							}
						});

				ChannelFuture f = b.connect(HOST, PORT).sync();

				// Wait until the connection is closed.
				f.channel().closeFuture().sync();
			} finally {
				// Shut down the event loop to terminate all threads.
				group.shutdownGracefully();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

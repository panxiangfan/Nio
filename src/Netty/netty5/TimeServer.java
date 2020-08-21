package Netty.netty5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {
	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup) // 添加工作线程组
					.channel(NioServerSocketChannel.class) // 设置管道模式
					.option(ChannelOption.SO_BACKLOG, 1024) // 配置BLOCK大小
					.childHandler(new ChildChannelHandler());

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			 //自定义分隔符解码器,要是到达最大长度还未发现分隔符则抛出异常
			/*ByteBuf delimiter = Unpooled.copiedBuffer("_#_".getBytes());
			arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(100,delimiter))*/
			// arg0.pipeline().addLast(new FixedLengthFrameDecoder(30)); //定长解码器
			arg0.pipeline().addLast(new LineBasedFrameDecoder(1024)); // 换行符解码器,要是到达1024之后还未匹配到换行符则会抛出异常
			arg0.pipeline().addLast(new StringDecoder());
			arg0.pipeline().addLast(new TimeServerHandler());
		}

	}

	public static void main(String[] args) throws Exception {
		new TimeServer().bind(8081);
	}
}

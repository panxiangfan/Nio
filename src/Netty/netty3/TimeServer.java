package Netty.netty3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class TimeServer {
	public void bind(int port) throws Exception
	{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)  //添加工作线程组
			 .channel(NioServerSocketChannel.class)   //设置管道模式
			 .option(ChannelOption.SO_BACKLOG, 1024)  //配置BLOCK大小
			 .childHandler(new ChildChannelHandler());
			
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		}finally
		{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>
	{

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			arg0.pipeline().addLast(new TimeServerHandler());
		}
		
	}
	
	public static void main(String[] args)throws Exception {
		new TimeServer().bind(7070);
	}
}

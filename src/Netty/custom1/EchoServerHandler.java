package Netty.custom1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * Sharable表示此对象在channel间共享 handler类是我们的具体业务类
 * */
//@Sharable
// 注解@Sharable可以让它在channels间共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println(msg);
		System.out.println();
		System.out.println();
		//ctx.write(msg);// 写回数据，
	}
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("我是服务端连接成功");
	}
}
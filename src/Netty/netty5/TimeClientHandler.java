package Netty.netty5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

	private int counter;

	private byte[] req;

	public TimeClientHandler() {
		req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
	}

	// 通道激活时触发，当客户端connect成功后，服务端就会接收到这个事件，从而可以把客户端的Channel记录下来，供后面复用
	public void channelActive(ChannelHandlerContext ctx) {
		ByteBuf message = null;
		for (int i = 0; i < 200; i++) {
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}

	// 当收到对方发来的数据后，就会触发，参数msg就是发来的信息，可以是基础类型，也可以是序列化的复杂对象。
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String) msg;
		System.out.println("Now is : " + body + " ; the counter is :" + ++counter);
	}

	// 出错是会触发，做一些错误处理
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}

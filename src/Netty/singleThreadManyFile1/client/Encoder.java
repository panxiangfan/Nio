package Netty.singleThreadManyFile1.client;


import Netty.singleThreadManyFile1.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

//Encoder extends MessageToByteEncoder extends ChannelInboundHandlerAdapter extends  ChannelHandlerAdapter implements ChannelHandler
public class Encoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
		if(message.getDirectory() == null){
			out.writeInt(1);
			out.writeInt(message.getNameLength());
			out.writeBytes(message.getName().getBytes());
			out.writeLong(message.getContentLength());
		}else{//文件夹
			out.writeInt(0);
			out.writeInt(message.getDirectoryLength());
			out.writeBytes(message.getDirectory().getBytes());
		}
	}
}
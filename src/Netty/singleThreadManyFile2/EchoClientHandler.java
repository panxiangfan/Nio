package Netty.singleThreadManyFile2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedFile;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	private String basepath = "C:\\Users\\Administrator\\Desktop\\apache-tomcat-8.0.48";
	private String filePath;  //文件路径
	private int mark;
	
	public EchoClientHandler(String filePath,int mark) {
		this.filePath = filePath;
		this.mark = mark;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		Message message = new Message();
		message.setMark(mark);
		if(mark == 1)
		{
			message.setFileLength(new File(filePath).length());
		}
		message.setFilePath(filePath.replace(basepath, ""));
		message.setFilePathLength(filePath.replace(basepath, "").getBytes().length);
		
		ctx.writeAndFlush(message);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String test = (String) msg;
		if(test.equals("b"))
		{
			ctx.close();
		}
		else if (test.equals("a"))
		{
				RandomAccessFile raf = null;
				try {
					raf = new RandomAccessFile(filePath, "r");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					ctx.writeAndFlush(new ChunkedFile(raf)).addListener(new ChannelFutureListener(){
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							future.channel().close();
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

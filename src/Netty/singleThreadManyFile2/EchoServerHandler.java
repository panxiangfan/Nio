package Netty.singleThreadManyFile2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	private boolean first = true;
	private FileOutputStream fos;
	private BufferedOutputStream bufferedOutputStream;
	private static String prefix = "C:\\Users\\Administrator\\Desktop\\新建文件夹\\";
	private String OK = "ok";
	private long fileLength = 0;
	private long sum= 0;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(1);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {

		//System.out.println("===============" + ctx.hashCode());
		
		try {
			if (msg instanceof Message) {
				Message ms = (Message) msg;
				// 文件夹
				if (ms.getMark() == 0) {
					File file = new File(prefix + ms.getFilePath());
					if (!file.exists()) {
						file.mkdirs();
					}

					ctx.writeAndFlush("b");
					Decord.mark.put(ctx, true);
					ctx.close();
					return ;

				} else {
					File file = new File(prefix + ms.getFilePath());
					if (!file.exists()) {
						file.createNewFile();
					}
					
					String test = "a";
					
					fileLength = ms.getFileLength();
					if(fileLength == 0)
					{
						Decord.mark.put(ctx, true);
						test = "b";
						ctx.writeAndFlush(test);
						ctx.close();
						return ;
					}
					ctx.writeAndFlush(test);
					
					
					if(fileLength != 0)
					{
						fos =  new FileOutputStream(file);
						bufferedOutputStream = new BufferedOutputStream(fos);
					}
					return ;
				}
			} else {
				
				byte[] bytes= (byte[]) msg;
				sum = sum + bytes.length;
				bufferedOutputStream.write(bytes, 0, bytes.length);
				bufferedOutputStream.flush();
	    		
	    		if(sum == fileLength)
	    		{
	    			Decord.mark.put(ctx, true);
	    			bufferedOutputStream.close();
					ctx.writeAndFlush("b");
					ctx.close();
	    		}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		ByteBuf buf = (ByteBuf) msg;
//		
//		byte[] bytes = new byte[buf.readableBytes()];  //readableBytes 可读的实际内容长度
//		buf.readBytes(bytes);
//		if (first) {
//			
//			String filePath = new String(bytes);
//			System.out.println("  传输的文件名称：    "+filePath);
//			first = false;
//			File file = new File(prefix + filePath);
//			
//			if (!file.exists()) { //判断是否是一个文件，如果没有此文件就创建
//				try { 
//					file.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			try {
//				fos =  new FileOutputStream(file);
//				bufferedOutputStream = new BufferedOutputStream(fos);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			ctx.writeAndFlush(OK.getBytes());
//			buf.release();
//			return;
//		}
//		
//    	try {
//    		bufferedOutputStream.write(bytes, 0, bytes.length);
//    		buf.release();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// bufferedOutputStream.flush();
		// bufferedOutputStream.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

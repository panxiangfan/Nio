package Netty.singleThreadManyFile1.server;

import Netty.singleThreadManyFile1.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class Decord extends ByteToMessageDecoder {
	int HEAD_LENGTH = 4;
	private long contentSumLength = 0;
	private long contentLength = 0;
	public static boolean mark = true;

	//不管是头信息,还是我们普通的流信息,所有信息都会进入到这里
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		//包括第一个文件的内容,第二文件的头信息,第二个文件的内容,第三文件的头信息,第三个文件的内容
		//先接收头,如果头信息接收完成了.设置为false
		//1024  -4 =1020
		if(mark){
			in.markReaderIndex(); // 我们标记一下当前的readIndex的位置					// 的readInt()方法会让他的readIndex增加4
			
			int data = in.readableBytes();
			
			if (data < 4) {
				in.resetReaderIndex();//返回以前标记,到时再重新读取.还会与下次的接收值累加
				return;
			}
			//文件夹,文件夹,文件,文件,文件夹
			
			int state = in.readInt();
			
			if(state == 0){//文件夹
				try {

					data = in.readableBytes();
					if (data < 4) {
						in.resetReaderIndex();//返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					int dataLength = in.readInt(); // 读取传送过来的消息的长度。ByteBuf
					if (dataLength < 0) { // 我们读到的int为负数，是不应该出现的情况，这里出现这情况，关闭连接。
						in.resetReaderIndex();//返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					if (in.readableBytes() < dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
						in.resetReaderIndex();//返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					byte[] body = new byte[dataLength];
					in.readBytes(body);

					Message message = new Message();
					message.setDirectory(new String(body));
					out.add(message);

				} catch (Exception e) {
					System.err.println("抛出了异常---Decord");
				}
			}else{
				try {

					data = in.readableBytes();
					if (data < HEAD_LENGTH) { // 这个HEAD_LENGTH是我们用于表示头长度的字节数。
						in.resetReaderIndex();//返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					int dataLength = in.readInt(); // 读取传送过来的消息的长度。ByteBuf
					if (dataLength < 0) { // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
						in.resetReaderIndex();//返回以前标记,到时再重新读取.还会与下次的接收值累加\
						return ;
					}

					if (in.readableBytes() < dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
						in.resetReaderIndex();//返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					byte[] body = new byte[dataLength];
					in.readBytes(body);

					Message message = new Message();
					message.setName(new String(body));


					long contentLength = in.readLong();							
					message.setContentLength(contentLength);

					contentSumLength = contentLength;
					
					
					if(contentLength != 0){
						mark = false;
					}
					out.add(message);

				} catch (Exception e) {
					System.err.println("抛出了异常---Decord");
				}
			}
		}else{
			//如果我们总长度减去累加的长度,如果小于可读长度(可读长度里面有一部分是其它的文件.我们这个时候,只读当前这个文件自己的长度)
			//800-400=400    1000-600=400
			if(in.readableBytes() > contentSumLength - contentLength){
	    		//buf.skipBytes(buf.readableBytes());
	    		byte[] bytes = new byte[Integer.valueOf(String.valueOf(contentSumLength - contentLength))];
	    		in.readBytes(bytes);
	    		contentSumLength = 0;
	    		contentLength = 0;
	    		out.add(bytes);
			}else{//如果文件的总长度,大于我们可读与累加的长度
				
				byte[] bytes = new byte[in.readableBytes()];
				contentLength = contentLength + bytes.length;
	    		in.readBytes(bytes);
	    		if(contentLength == contentSumLength){
	    			contentLength = 0;
	    			contentSumLength = 0;
	    		}
	    		out.add(bytes);
			}
		}
	}
}
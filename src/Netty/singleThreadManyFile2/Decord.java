package Netty.singleThreadManyFile2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Decord extends ByteToMessageDecoder {
	public static Map<ChannelHandlerContext,Boolean> mark = new HashMap<>();

	// 不管是头信息,还是我们普通的流信息,所有信息都会进入到这里
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 包括第一个文件的内容,第二文件的头信息,第二个文件的内容,第三文件的头信息,第三个文件的内容
		// 先接收头,如果头信息接收完成了.设置为false
		System.out.println(mark.get(ctx) + " === " + ctx.hashCode());
		if (mark.get(ctx) == null ? true : mark.get(ctx)) {
			mark.put(ctx, false);
			
			in.markReaderIndex(); // 我们标记一下当前的readIndex的位置 // 的readInt()方法会让他的readIndex增加4

			int state = in.readInt();

			if (state == 0) {// 文件夹
				try {

					int data = in.readableBytes();
					if (data < 4) {
						in.resetReaderIndex();// 返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					int filePathLength = in.readInt(); // 读取传送过来的消息的长度。ByteBuf
					if (filePathLength < 0) { // 我们读到的int为负数，是不应该出现的情况，这里出现这情况，关闭连接。
						in.resetReaderIndex();// 返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					if (in.readableBytes() < filePathLength) { // 读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex.
																// 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
						in.resetReaderIndex();// 返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					byte[] body = new byte[filePathLength];
					in.readBytes(body);

					Message message = new Message();
					message.setMark(state);
					message.setFilePath(new String(body));
					out.add(message);

				} catch (Exception e) {
					System.err.println("抛出了异常---Decord");
				}
			} else {
				try {

					int data = in.readableBytes();
					if (data < 4) {
						in.resetReaderIndex();// 返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					int filePathLength = in.readInt(); // 读取传送过来的消息的长度。ByteBuf
					if (filePathLength < 0) { // 我们读到的int为负数，是不应该出现的情况，这里出现这情况，关闭连接。
						in.resetReaderIndex();// 返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					if (in.readableBytes() < filePathLength) { // 读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex.
																// 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
						in.resetReaderIndex();// 返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}

					byte[] body = new byte[filePathLength];
					in.readBytes(body);

					data = in.readableBytes();
					if (data < 8) {
						in.resetReaderIndex();// 返回以前标记,到时再重新读取.还会与下次的接收值累加
						return;
					}
					long fileLength = in.readLong(); // 读取传送过来的消息的长度。ByteBuf

					Message message = new Message();
					message.setMark(state);
					message.setFilePath(new String(body));
					message.setFileLength(fileLength);
					out.add(message);

				} catch (Exception e) {
					System.err.println("抛出了异常---Decord");
				}
			}
		} else {
			byte[] bytes = new byte[in.readableBytes()];
			in.readBytes(bytes);
			out.add(bytes);
		}
	}
}
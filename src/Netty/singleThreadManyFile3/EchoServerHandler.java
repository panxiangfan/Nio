/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package Netty.singleThreadManyFile3;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	private FileOutputStream fos;
	private BufferedOutputStream bufferedOutputStream;
	private static String prefix = "C:\\Users\\Administrator\\Desktop\\测试";

	private String OK = "ok";
	private long contentLength = 0;
	private long contentSumLength = 0;//获得内容的字节数
	
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof Message) {
			Message message = (Message) msg;
			if(message.getDirectory() != null){
				File file = new File(prefix + message.getDirectory());
				if (!file.exists()) {
					file.mkdirs();//创建文件夹
				}
			}else{
				contentSumLength = message.getContentLength();

				File file = new File(prefix + File.separator +message.getName());
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(message.getContentLength() > 0){
					try {
						fos =  new FileOutputStream(file);
						bufferedOutputStream = new BufferedOutputStream(fos);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return;
		}
    	try {
    		byte[] bytes= (byte[]) msg;
    		contentLength = contentLength + bytes.length;
    		bufferedOutputStream.write(bytes, 0, bytes.length);
    		bufferedOutputStream.flush();
    		//100  10 10 10 10 10 10 10 10 10 10
    		if(contentLength == contentSumLength)
    		{
    			contentLength = 0 ;
    			contentSumLength = 0;
    			bufferedOutputStream.close();
    		}
    		//buf.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		bufferedOutputStream.flush();
		bufferedOutputStream.close();
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

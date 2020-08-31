package Netty.custom3;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;  

/**
 * 编码
 * @作者  罗玲红
 *
 * @时间 2017年6月14日 上午11:24:31
 */
public class CustomEncoder extends MessageToByteEncoder<CustomMsg> {  
  
    @Override  
    protected void encode(ChannelHandlerContext ctx, CustomMsg msg, ByteBuf out) throws Exception {  
        if(null == msg){  
            throw new Exception("msg is null");  
        }  
        out.writeInt(msg.getNameLength());
        out.writeBytes(msg.getName().getBytes());
        out.writeInt(msg.getBodyLength());
        out.writeBytes(msg.getBody().getBytes());
 
         
    }  
  
}  
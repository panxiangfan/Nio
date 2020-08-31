package Netty.custom3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * netty-消息头和消息体解码器
 * @作者  罗玲红
 *
 * @时间 2017年6月14日 上午11:22:40
 */
public class CustomDecoder extends LengthFieldBasedFrameDecoder {  
      
    //判断传送客户端传送过来的数据是否按照协议传输，头部信息的大小应该是 byte+byte+int = 1+1+4 = 6  
    private static final int HEADER_SIZE = 8;  
      
    private byte type;  
    

    /** 
     *  
     * @param maxFrameLength 解码时，处理每个帧数据的最大长度 
     * @param lengthFieldOffset 该帧数据中，存放该帧数据的长度的数据的起始位置 
     * @param lengthFieldLength 记录该帧数据长度的字段本身的长度 
     * @param lengthAdjustment 修改帧数据长度字段中定义的值，可以为负数 
     * @param initialBytesToStrip 解析的时候需要跳过的字节数 
     * @param failFast 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常 
     */  
    
    public CustomDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,  
            int lengthAdjustment, int initialBytesToStrip, boolean failFast) {  
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,  
                lengthAdjustment, initialBytesToStrip, failFast);
        
    }  
      
    @Override  
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {  
        if (in == null) {  
            return null;  
        }  
        if (in.readableBytes() < HEADER_SIZE) {  
            throw new Exception("可读信息段比头部信息都小，你在逗我？");  
        }  
          
        //注意在读的过程中，readIndex的指针也在移动  
       int nameLength = in.readInt();
       System.out.println("namelength === " + nameLength);
       
        if (in.readableBytes() < nameLength) {  
            throw new Exception("body字段你告诉我长度是"+nameLength+",但是真实情况是没有这么多，你又逗我？");  
        }  
        ByteBuf buf = in.readBytes(nameLength);  
        byte[] req = new byte[buf.readableBytes()];  
        buf.readBytes(req);  
        String name = new String(req, "UTF-8");  
        
        
        int bodyLength = in.readInt();
        System.out.println("bodyLength === " + bodyLength);
        
        
        
        ByteBuf buf1 = in.readBytes(bodyLength);  
        byte[] req1 = new byte[buf1.readableBytes()];  
        buf1.readBytes(req1);  
        String body = new String(req1, "UTF-8");  
          
        CustomMsg customMsg = new CustomMsg(nameLength,name,bodyLength,body);  
        return customMsg;  
    }  
  
}  
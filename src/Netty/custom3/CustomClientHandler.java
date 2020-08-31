package Netty.custom3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;  
  
/**
 * 客户端
 * @作者  罗玲红
 *
 * @时间 2017年6月14日 上午11:29:10
 */
public class CustomClientHandler extends ChannelInboundHandlerAdapter {  
      
    @Override  
    public void channelActive(ChannelHandlerContext ctx) throws Exception {  
    	String name = "aaaaaa";
    	String body = "woshizhongguoren";
        CustomMsg customMsg = new CustomMsg(name.length(),name,body.length(),body); 
        ctx.writeAndFlush(customMsg);  
        
    }  
  
}  
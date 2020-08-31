package Netty.custom1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;  
  
@Sharable  
public class EchoClientHandler extends ChannelInboundHandlerAdapter {  
    /** 
     *此方法会在连接到服务器后被调用  
     * */  
    public void channelActive(ChannelHandlerContext ctx) {  
    	for(int i =0;i<1000;i++)
    	{
    		ctx.writeAndFlush("test123456");
    	}
        
    }

	  
  
  
}  
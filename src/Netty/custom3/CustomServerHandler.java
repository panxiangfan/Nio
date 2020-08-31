package Netty.custom3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;  
/**
 * 服务端  
 * @作者  罗玲红
 *
 * @时间 2017年6月14日 上午11:29:21
 */
public class CustomServerHandler extends SimpleChannelInboundHandler<Object> {  
  
    @Override  
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {  
        if(msg instanceof CustomMsg) {  
            CustomMsg customMsg = (CustomMsg)msg;  
            System.out.println(customMsg.toString());
          //  System.out.println("-----------------");
            //System.out.println("客户端--->服务端:  "+ctx.channel().remoteAddress()+" 发送  "+customMsg.getBody());  
        }  
          
    }  
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	cause.printStackTrace();
    	System.out.println("aaaaaaaaaaaaaaaaaaaa");
	}
  
}  
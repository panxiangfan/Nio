package Netty.custom1;
import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
  
public class EchoClient {  
    private final String host;  
    private final int port;  
  
    public EchoClient(String host, int port) {  
        this.host = host;  
        this.port = port;  
    }  
  
    public void start() throws Exception {  
        EventLoopGroup group = new NioEventLoopGroup();  
        try {  
            Bootstrap b = new Bootstrap();  
            b.group(group);  
            b.channel(NioSocketChannel.class);  
            b.remoteAddress(new InetSocketAddress(host, port));  
            b.handler(new ChannelInitializer<SocketChannel>() {  
  
                public void initChannel(SocketChannel ch) throws Exception {  
                	//ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 4));    
                	//自动加上头
                	ch.pipeline().addLast(new LengthFieldPrepender(2));    
                	ch.pipeline().addLast(new StringEncoder()); 
                	ch.pipeline().addLast(new StringDecoder()); 
                    ch.pipeline().addLast(new EchoClientHandler());  
                }  
            });  
            ChannelFuture f = b.connect().sync();  
            f.addListener(new ChannelFutureListener() {  
                  
                public void operationComplete(ChannelFuture future) throws Exception {  
                    if(future.isSuccess()){  
                    }else{  
                        future.cause().printStackTrace();  
                    }  
                      
                }  
            });  
            f.channel().closeFuture().sync();  
        } finally {  
            group.shutdownGracefully().sync();  
        }  
    }  
  
    public static void main(String[] args) throws Exception {  
      
        new EchoClient("127.0.0.1", 6666).start();
    }  
}  
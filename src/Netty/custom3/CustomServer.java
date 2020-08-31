package Netty.custom3;
import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;  
  
public class CustomServer {  
      
	/**
	 * 处理每个帧数据的最大长度 
	 */
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;  
    /**
     * 记录该帧数据长度的字段本身的长度 
     */
    private static final int LENGTH_FIELD_LENGTH = 4; 
    /**
     *  该帧数据中，存放该帧数据的长度的数据的起始位置 
     */
    private static final int LENGTH_FIELD_OFFSET = 2;  
    /**
     * 修改帧数据长度字段中定义的值，可以为负数
     */
    private static final int LENGTH_ADJUSTMENT = 0;  
    /**
     * 解析的时候需要跳过的字节数 
     */
    private static final int INITIAL_BYTES_TO_STRIP = 0;  
    /**
     * 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，
     * 为false，读取完整个帧再报异常 
     */
    private static final boolean failFast=false;
    
    /**
     * 端口
     */
    private int port;  
      
    public CustomServer(int port) {  
        this.port = port;  
    }  
      
    public void start(){  
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  
        EventLoopGroup workerGroup = new NioEventLoopGroup();  
        try {  
            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port))  
                    .childHandler(new ChannelInitializer<SocketChannel>() {  
                          
                        protected void initChannel(SocketChannel ch) throws Exception {  
                        	 ch.pipeline().addLast(new CustomDecoder(1024*1024,0,0,0,0,true));
                             ch.pipeline().addLast(new CustomServerHandler());  
                        };  
                          
                    }).option(ChannelOption.SO_BACKLOG, 128)     
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  
             // 绑定端口，开始接收进来的连接  
             ChannelFuture future = sbs.bind(port).sync();    
               
             System.out.println("服务开始监听：   " + port+" 端口" );  
             future.channel().closeFuture().sync();  
        } catch (Exception e) {  
            bossGroup.shutdownGracefully();  
            workerGroup.shutdownGracefully();  
        }  
    }  
      
    //9(4), helloword     (xxxx)helloword
    
    //9 
    //hello word
    //(22)helloword
    public static void main(String[] args) throws Exception {  
        int port;  
        if (args.length > 0) {  
            port = Integer.parseInt(args[0]);  
        } else {  
            port = 8080;  
        }  
        new CustomServer(port).start();  
    }  
}  
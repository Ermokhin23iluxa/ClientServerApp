package org.test_task_client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.test_task_client.configuration.Config;

public class Network {
    private SocketChannel channel;
    private static final String HOST = Config.getHost();
    private static final int PORT = Config.getPort();

    public Network(){
        new Thread(()->{
            EventLoopGroup workerGroup = new NioEventLoopGroup(1);
            try{
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new StringDecoder(),
                                        new StringEncoder(),
                                        new ClientHandler());

                            }
                        });
                ChannelFuture future = b.connect(HOST,PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
    }
    public void sendMsg(String msg){
        if(channel!=null && channel.isActive()){
            channel.writeAndFlush(msg);
        } else {
            System.err.println("Канал не готов");
        }

    }
}

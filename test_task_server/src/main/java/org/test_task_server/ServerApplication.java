package org.test_task_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.test_task_server.commandLayer.CommandManager;
import org.test_task_server.commandLayer.executableСommand.serverCommands.ConsoleCommandLoop;
import org.test_task_server.configuration.Config;
import org.test_task_server.handlers.MainHandler;
import org.test_task_server.service.TopicService;
import org.test_task_server.service.UserService;
import org.test_task_server.service.VotingService;

public class ServerApplication {

    public static void main(String[] args) {
        int port = Config.getPort();
        UserService userService = new UserService();
        TopicService topicService = new TopicService();
        VotingService votingService = new VotingService(topicService);
        CommandManager commandManager = new CommandManager(userService,topicService,votingService);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("decoder", new StringDecoder());
                            socketChannel.pipeline().addLast("encoder", new StringEncoder());
                            socketChannel.pipeline().addLast("mainHandler", new MainHandler(commandManager));
                        }
                    });
            ChannelFuture future = b.bind(port).sync();

            System.out.println("Сервер запущен на порту: "+port);

            // Запускаем консольный цикл команд сервера
            Thread consoleThread = new Thread(new ConsoleCommandLoop(topicService));
            consoleThread.setDaemon(true);
            consoleThread.start();

            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}

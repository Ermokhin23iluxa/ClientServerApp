package org.test_task_server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.test_task_server.commandLayer.CommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class MainHandler extends SimpleChannelInboundHandler<String> {

    private static final List<Channel> channels = new ArrayList<>();
    private static final AtomicInteger clientIndex = new AtomicInteger(1);
    private final  CommandManager commandManager;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клинет подключился: "+ ctx);
        channels.add(ctx.channel());
        String clientName = "Клиент #" + clientIndex.getAndIncrement();
        System.out.println(clientName);
    }

    // метод чтения сообещния поступаемого от клиента
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println("Получено сообщение: "+ s);
        String response = commandManager.handleCommand(s, ctx);
        ctx.writeAndFlush(response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

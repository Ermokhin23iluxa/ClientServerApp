package org.test_task_server.commandLayer.executableСommand.serverCommands;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.executableСommand.Command;

@RequiredArgsConstructor
public class ExitServerCommand implements Command {
    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        ctx.writeAndFlush("Сервер завершает работу...");
        ctx.close();
        System.exit(0);
        return "";
    }
}

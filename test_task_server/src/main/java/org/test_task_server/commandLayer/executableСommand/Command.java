package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;

public interface Command {
    String execute(String[]args, ChannelHandlerContext ctx);
}

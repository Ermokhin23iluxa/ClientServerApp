package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.attribute.ChannelAttributes;
import org.test_task_server.service.UserService;

@RequiredArgsConstructor
public class ExitCommand implements Command {
    private final UserService userService;

    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        String username = ctx.channel().attr(ChannelAttributes.USERNAME).get();
        if (username != null) {
            userService.logout(username);
        }
        ctx.writeAndFlush("До свидания!\n");
        ctx.close();
        return "";
    }
}

package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.attribute.ChannelAttributes;
import org.test_task_server.service.UserService;

@RequiredArgsConstructor
public class LoginCommand implements Command {
    private final  UserService userService;

    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        if (args.length < 2 || !args[1].startsWith("-u=")){
            return "Ошибка: Неверный формат команды. Пример: login -u=username";
        }
        String username = args[1].substring(3);
        if (username.isEmpty()) {
            return "Ошибка: Имя пользователя не может быть пустым. Пример: login -u=username";
        }
        Channel channel = ctx.channel();
        if (userService.login(username, channel)) {
            channel.attr(ChannelAttributes.USERNAME).set(username);
            return "Вы успешно вошли как " + username;
        } else {
            return "Ошибка: Пользователь с таким именем уже залогинен.";
        }
    }


}

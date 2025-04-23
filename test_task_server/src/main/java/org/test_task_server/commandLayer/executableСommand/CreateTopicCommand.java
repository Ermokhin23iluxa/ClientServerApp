package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.attribute.ChannelAttributes;
import org.test_task_server.service.TopicService;

@RequiredArgsConstructor
public class CreateTopicCommand implements Command {

    private final TopicService topicService;

    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        if (ctx.channel().attr(ChannelAttributes.USERNAME).get() == null) {
            return "Ошибка: Не выполнен вход. Сначала выполните login.";
        }

        if (args.length < 3 || !args[2].startsWith("-n=") || args.length>3) {
            return "Ошибка: Неверный формат команды. Пример: create topic -n=InterestingPlace";
        }

        String topicName = args[2].substring(3);

        if (topicName.isEmpty()) {
            return "Ошибка: Название темы не может быть пустым. Пример: topic -n=InterestingPlace";
        }
        boolean created = topicService.createTopic(topicName);
        if (created) {
            return "Раздел '" + topicName + "' успешно создан";
        } else {
            return "Ошибка: Раздел с именем '" + topicName + "' уже существует.";
        }
    }
}

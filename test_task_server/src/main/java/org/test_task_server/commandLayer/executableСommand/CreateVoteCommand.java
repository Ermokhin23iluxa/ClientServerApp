package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.attribute.ChannelAttributes;
import org.test_task_server.commandLayer.entity.Vote;
import org.test_task_server.handlers.VoteCreationHandler;
import org.test_task_server.service.TopicService;

@RequiredArgsConstructor
public class CreateVoteCommand implements Command {

    private final TopicService topicService;

    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        // Проверяем, что пользователь вошёл в систему
        if (ctx.channel().attr(ChannelAttributes.USERNAME).get() == null) {
            return "Ошибка: Не выполнен вход. Сначала выполните login.";
        }
        // Ожидается: create vote -t=<topic>
        if (args.length < 3 || !args[2].startsWith("-t=") || args.length>3) {
            return "Ошибка: Неверный формат команды. Пример: create vote -t=<topic>";
        }
        String topicName = args[2].substring(3);
        if (topicName.isEmpty()) {
            return "Ошибка: Название раздела не может быть пустым.";
        }

        String username = ctx.channel().attr(ChannelAttributes.USERNAME).get();

        if(topicService.getTopic(topicName)==null){
            return "Ошибка: Раздел '" + topicName + "' не существует.";
        }
        ctx.pipeline()
                .addBefore("mainHandler",
                        "VoteCreationHandler",
                        new VoteCreationHandler(topicName, username, topicService));
        return "";
    }
}

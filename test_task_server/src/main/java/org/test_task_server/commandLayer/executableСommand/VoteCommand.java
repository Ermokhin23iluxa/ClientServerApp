package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.attribute.ChannelAttributes;
import org.test_task_server.commandLayer.entity.Topic;
import org.test_task_server.commandLayer.entity.Vote;
import org.test_task_server.handlers.VoteOptionHandler;
import org.test_task_server.service.TopicService;
import org.test_task_server.service.VotingService;
@RequiredArgsConstructor
public class VoteCommand implements Command{
    private final VotingService votingService;
    private final TopicService topicService;

    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        // Ожидаемый формат команды: vote -t=<topic> -v=<vote>
        if (args.length < 3 || !args[1].startsWith("-t=") || !args[2].startsWith("-v=")) {
            return "Ошибка: Неверный формат команды. Пример: vote -t=<topic> -v=<vote>";
        }
        String topicName = args[1].substring(3);
        String voteName = args[2].substring(3);
        // Получаем имя текущего пользователя из атрибутов канала
        String username = ctx.channel().attr(ChannelAttributes.USERNAME).get();
        if (username == null) {
            return "Ошибка: Не выполнен вход. Сначала выполните login.";
        }
        // Вызываем метод голосования (его реализация в VotingService должна вернуть true, если голос принят)

        ctx.pipeline().addBefore("mainHandler", "VoteOptionHandler",
                new VoteOptionHandler(topicName, voteName, username, votingService));

        Topic topic = topicService.getTopic(topicName);
        Vote vote = topic.getVote(voteName);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<vote.getOptions().size();++i){
            sb.append("Вариант ")
                    .append(i + 1)
                    .append(": ")
                    .append(vote.getOptions().get(i))
                    .append("\n");
        }
        return sb.toString();
    }
}

package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.attribute.ChannelAttributes;
import org.test_task_server.service.TopicService;

@RequiredArgsConstructor
public class ViewCommand implements Command{

    private final TopicService topicService;

    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        if(ctx.channel().attr(ChannelAttributes.USERNAME).get()==null){
            return "Ошибка: Не выполнен вход. Сначала выполните login.";
        }
        if(args.length==1 && args[0].equals("view")){
            return topicService.getTopics().values().toString();
        }
        if(args.length>=2 && args[1].startsWith("-t=")){
            String topicName = args[1].substring(3);
            if(topicName.isEmpty()){
                return "Ошибка: название раздела не может быть пустым.";
            }
            if(args.length==2){
                return topicService.getTopicVotes(topicName);
            }
            if(args.length==3 && args[2].startsWith("-v=")){
                String vote = args[2].substring(3);
                return topicService.getVoteInfo(topicName,vote);
            }
        }
        return "Ошибка: неверный формат команды view.";
    }
}

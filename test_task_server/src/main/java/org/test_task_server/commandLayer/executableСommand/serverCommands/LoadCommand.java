package org.test_task_server.commandLayer.executableСommand.serverCommands;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.executableСommand.Command;
import org.test_task_server.service.TopicService;

@RequiredArgsConstructor
public class LoadCommand implements Command {
    private final TopicService topicService;
    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        if(args.length!=2){
            return "Ошибка: неверный формат. Пример: load <filename>";
        }
        String filename = args[1];
        boolean ok = topicService.loadFromFile(filename);
        return ok
                ? "Данные успешно загружены из " + filename
                : "Ошибка при загрузке из файла " + filename;
    }
}

package org.test_task_server.commandLayer.executableСommand.serverCommands;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.executableСommand.Command;
import org.test_task_server.service.TopicService;
@RequiredArgsConstructor
public class SaveCommand implements Command {
    private final TopicService topicService;
    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        if(args.length !=2){
            return "Ошибка: неверный формат. Пример: save <filename>";
        }
        String filename = args[1];
        boolean ok = topicService.saveToFile(filename);
        return ok
                ? "Данные успешно сохранены в " + filename
                : "Ошибка при сохранении в файл " + filename;
    }
}

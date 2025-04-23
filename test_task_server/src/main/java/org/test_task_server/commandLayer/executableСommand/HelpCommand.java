package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;

public class HelpCommand implements Command {
    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        return """
                Список команд:
                login -u=<username>       : Вход на сервер (остальные команды доступны после входа)
                create topic -n=<topic>   : Создать раздел с указанным именем
                view                      : Показать список разделов (и число голосований)
                view -t=<topic>           : Показать голосования в разделе
                create vote -t=<topic>    : Создать голосование в разделе
                view -t=<topic> -v=<vote> : Показать информацию о голосовании
                vote -t=<topic> -v=<vote> : Проголосовать в указанном голосовании
                delete -t=<topic> -v=<vote> : Удалить голосование (может только автор)
                exit                      : Выход из программы
                help                      : Показать список команд
                """;
    }
}

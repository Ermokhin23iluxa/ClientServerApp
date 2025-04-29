package org.test_task_server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import org.test_task_server.service.VotingService;

import java.util.List;

@RequiredArgsConstructor
public class VoteOptionHandler extends SimpleChannelInboundHandler<String> {
    private final String topicName;
    private final String voteName;
    private final String username;
    private final VotingService votingService;
    private final List<String> options;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("Введите вариант, за который голосуете:");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String line = msg.trim();
        int idx;
        try{
            idx = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            ctx.writeAndFlush("Ошибка: введите число от 1 до " + options.size() + ":\n");
            return;
        }
        if (idx < 1 || idx > options.size()) {
            ctx.writeAndFlush("Ошибка: в диапазоне 1–" + options.size() + ". Повторите:\n");
            return;
        }

        if (line.isEmpty()) {
            ctx.writeAndFlush("Вариант не может быть пустым. Повторите ввод:");
            return;
        }
        String chosenOption = options.get(idx-1);
        boolean success = votingService.vote(topicName, voteName, username, chosenOption);
        if (success) {
            ctx.writeAndFlush("Ваш голос учтен в голосовании '" + voteName + "' раздела '" + topicName + "'.");
        } else {
            ctx.writeAndFlush("Ошибка: Не удалось зарегистрировать голос. Проверьте корректность выбранного варианта.");
        }
        // После обработки пользовательского ввода удаляем этот хэндлер
        ctx.pipeline().remove(this);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.writeAndFlush("Ошибка при регистрации голоса: " + cause.getMessage() );
        ctx.pipeline().remove(this);
    }
}

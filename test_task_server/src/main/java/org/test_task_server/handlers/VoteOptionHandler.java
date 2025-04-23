package org.test_task_server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import org.test_task_server.service.VotingService;

@RequiredArgsConstructor
public class VoteOptionHandler extends SimpleChannelInboundHandler<String> {
    private final String topicName;
    private final String voteName;
    private final String username;
    private final VotingService votingService;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("Введите вариант, за который голосуете:\n");
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String chosenOption = msg.trim();
        if (chosenOption.isEmpty()) {
            ctx.writeAndFlush("Вариант не может быть пустым. Повторите ввод:");
            return;
        }
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.writeAndFlush("Ошибка при регистрации голоса: " + cause.getMessage() );
        ctx.pipeline().remove(this);
    }
}

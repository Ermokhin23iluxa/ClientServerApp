package org.test_task_server.commandLayer.executableСommand;

import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.attribute.ChannelAttributes;
import org.test_task_server.service.VotingService;
@RequiredArgsConstructor
public class DeleteVoteCommand implements Command{

    private final VotingService votingService;

    @Override
    public String execute(String[] args, ChannelHandlerContext ctx) {
        // Ожидаемый формат команды: delete -t=<topic> -v=<vote>
        if (args.length < 3 || !args[1].startsWith("-t=") || !args[2].startsWith("-v=") || args.length>3) {
            return "Ошибка: Неверный формат команды. Пример: delete -t=<topic> -v=<vote>";
        }
        String topicName = args[1].substring(3);
        String voteName = args[2].substring(3);
        // Получаем имя текущего пользователя из атрибутов канала
        String currentUser = ctx.channel().attr(ChannelAttributes.USERNAME).get();
        if (currentUser == null) {
            return "Ошибка: Не выполнен вход. Сначала выполните login.";
        }
        // Вызываем метод удаления голосования; метод должен проверять, что голосование существует и что currentUser является его создателем.
        boolean deleted = votingService.deleteVote(topicName, voteName, currentUser);
        return deleted
                ? "Голосование '" + voteName + "' в разделе '" + topicName + "' удалено."
                : "Ошибка: Не удалось удалить голосование. Возможно, голосование не существует или вы не являетесь его создателем.";
    }
}

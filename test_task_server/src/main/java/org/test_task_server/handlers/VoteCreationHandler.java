package org.test_task_server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.test_task_server.commandLayer.entity.Vote;
import org.test_task_server.service.TopicService;

import java.util.ArrayList;
import java.util.List;

public class VoteCreationHandler extends SimpleChannelInboundHandler<String> {

    private final String topicName;
    private final String username;
    private final TopicService topicService;

    private int step = 0;
    private boolean skipFirstMessage = true; // для игнорирования первого "лишнего" сообщения
    private String voteName;
    private String voteDescription;
    private int optionsCount = 0;
    private final List<String> options = new ArrayList<>();

    public VoteCreationHandler(String topicName, String username, TopicService topicService) {
        this.topicName = topicName;
        this.username = username;
        this.topicService = topicService;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // Когда хэндлер добавлен в pipeline, отправляем начальный запрос.
        ctx.writeAndFlush("Введите название голосования:");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String input = msg.trim();
        // Если первое сообщение пришло сразу и оно похоже на повторное указание команды, игнорируем его.
        if (skipFirstMessage) {
            // Если сообщение начинается с "create vote", пропускаем его
            if (msg.toLowerCase().startsWith("create vote")) {
                skipFirstMessage = false;
                return;
            }
            // Иначе – рассматриваем его как настоящее введённое значение
            skipFirstMessage = false;
        }

        //msg = msg.trim();// удаялет пробелы в конце и в начале


        switch (step) {
            case 0: // Сбор названия голосования
                if (input.isEmpty() || input.contains(" ")) {
                    ctx.writeAndFlush("Название голосования не может быть пустым или состоять из нескольких слов.Введите название голосования:");
                    return;
                }
                voteName = input;
                step++;
                ctx.writeAndFlush("Введите описание голосования (одно слово):");
                break;
            case 1: // Сбор описания голосования
                if (input.isEmpty() || input.contains(" ")) {
                    ctx.writeAndFlush("Ошибка: описание должно быть одним словом без пробелов. Повторите:\n");
                    return;
                }
                voteDescription = input;
                step++;
                ctx.writeAndFlush("Введите количество вариантов ответа (от 1 до 20):");
                break;
            case 2: // Сбор количества вариантов
                int n;
                try {
                    n = Integer.parseInt(input);
                    if (n < 1 || n > 20) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    ctx.writeAndFlush("Ошибка: введите число от 1 до 20. Повторите:");
                    return;
                }
//                try {
//
//                    n = Integer.parseInt(input);
//                    if (optionsCount <= 0) {
//                        ctx.writeAndFlush("Количество вариантов должно быть положительным числом. Введите количество вариантов ответа:");
//                        return;
//                    }
//                    if (optionsCount >= 20) {
//                        ctx.writeAndFlush("Количество вариантов должно быть меньше 21. Введите количество вариантов ответа:");
//                        return;
//                    }
//                } catch (NumberFormatException e) {
//                    ctx.writeAndFlush("Ошибка: количество вариантов должно быть числом. Введите количество вариантов ответа:");
//                    return;
//                }
                optionsCount= n;
                step++;
                ctx.writeAndFlush("Введите вариант 1:");
                break;
            default: // Сбор вариантов (шаги для сбора вариантов)
                if (input.isEmpty()) {
                    ctx.writeAndFlush("Вариант ответа не может быть пустым. Введите вариант " + (options.size() + 1) + ":");
                    return;
                }
                options.add(input);
                if (options.size() < optionsCount) {
                    ctx.writeAndFlush("Введите вариант " + (options.size() + 1) + ":");
                } else {
                    // Все данные собраны – создаем голосование
                    Vote vote = new Vote(voteName, voteDescription, username, options);
                    boolean added = topicService.addVoteInTopic(topicName, vote);
                    if (added) {
                        ctx.writeAndFlush("Голосование '" + voteName + "' создано в разделе '" + topicName + "' пользователем " + username + ".");
                    } else {
                        ctx.writeAndFlush("Ошибка: раздел '" + topicName + "' не найден.");
                    }
                    // Удаляем интерактивный хэндлер из pipeline
                    ctx.pipeline().remove(this);
                }
                break;

        }
        //ctx.pipeline().remove(this);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.writeAndFlush("Ошибка при создании голосования: " + cause.getMessage() + "\n");
        ctx.pipeline().remove(this);
    }

}

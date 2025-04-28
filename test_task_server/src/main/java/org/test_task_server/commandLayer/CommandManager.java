package org.test_task_server.commandLayer;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.test_task_server.commandLayer.executableСommand.*;
import org.test_task_server.service.TopicService;
import org.test_task_server.service.UserService;
import org.test_task_server.service.VotingService;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class CommandManager {
    private Map<String, Command> commands = new HashMap<>();

    public CommandManager(UserService userService, TopicService topicService, VotingService votingService) {
        commands.put("login", new LoginCommand(userService));
        commands.put("create topic", new CreateTopicCommand(topicService));
        commands.put("view", new ViewCommand(topicService));
        commands.put("create vote", new CreateVoteCommand(topicService));
        commands.put("vote", new VoteCommand(votingService,topicService));
        commands.put("delete", new DeleteVoteCommand(votingService));
        commands.put("help", new HelpCommand());
        commands.put("exit", new ExitCommand(userService));
    }

    public String handleCommand (String commandLine, ChannelHandlerContext ctx){
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return "Ошибка: пустая команда.";
        }

        String lowerLine = commandLine.toLowerCase().trim(); //привели к нижнему регистру
        String baseCommand = extractBaseCommand(lowerLine);// вернули команду

        if (baseCommand == null) {
            return "Неизвестная команда. Введите help для списка команд.";
        }
        Command command = commands.get(baseCommand);// достали команду из мапы
        if (command == null) {
            return "Неизвестная команда. Введите help для списка команд.";
        }
        try {
            String[] parts = commandLine.split(" ");
            return command.execute(parts, ctx);
        } catch (Exception e) {
            return "Ошибка при выполнении команды: " + e.getMessage();
        }
    }

    private String extractBaseCommand(String lowerLine) {
        if (lowerLine.startsWith("login")) {
            return "login";
        } else if (lowerLine.startsWith("create topic")) {
            return "create topic";
        } else if (lowerLine.startsWith("view")) {
            return "view";
        } else if (lowerLine.startsWith("create vote")) {
            return "create vote";
        } else if (lowerLine.startsWith("vote")) {
            return "vote";
        } else if (lowerLine.startsWith("delete")) {
            return "delete";
        } else if (lowerLine.startsWith("help")) {
            return "help";
        } else if (lowerLine.startsWith("exit")) {
            return "exit";
        }

        return null;
    }
}

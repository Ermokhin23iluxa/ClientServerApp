package org.test_task_server.commandLayer.executableСommand.serverCommands;

import lombok.RequiredArgsConstructor;
import org.test_task_server.service.TopicService;

import java.util.Scanner;

@RequiredArgsConstructor
public class ConsoleCommandLoop implements Runnable{
    private final TopicService topicService;
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите серверную команду (load <file>, save <file>, exit):");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(" ", 2);
            String cmd = parts[0].toLowerCase();
            switch (cmd) {
                case "load":
                    if (parts.length < 2) {
                        System.out.println("Использование: load <filename>");
                        break;
                    }
                    if (topicService.loadFromFile(parts[1])) {
                        System.out.println("Успешно загружено из " + parts[1]);
                    } else {
                        System.out.println("Ошибка загрузки из " + parts[1]);
                    }
                    break;
                case "save":
                    if (parts.length < 2) {
                        System.out.println("Использование: save <filename>");
                        break;
                    }
                    fds,f,fs,fs
                    if (topicService.saveToFile(parts[1])) {
                        System.out.println("Успешно сохранено в " + parts[1]);
                    } else {
                        System.out.println("Ошибка сохранения в " + parts[1]);
                    }
                    break;
                case "exit":
                    System.out.println("Сервер завершает работу...");
                    System.exit(0);
                    return;
                default:
                    System.out.println("Неизвестная команда. Доступно: load, save, exit");
            }
        }

    }
}

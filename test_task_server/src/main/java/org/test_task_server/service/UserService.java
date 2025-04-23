package org.test_task_server.service;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    // Потокобезопасная HashMap
    private final ConcurrentHashMap<String, Channel> users = new ConcurrentHashMap<>();

    // Метод логирования пользователя
    public boolean login(String username, Channel channel) {
        if(users.containsKey(username)){
            return false;
        }
        users.put(username, channel);
        return true;
    }

    public void logout(String username) {
        users.remove(username);
    }
}

package org.test_task_server.commandLayer.repository;

import org.test_task_server.commandLayer.entity.Topic;

import java.util.Map;


public interface TopicRepository {
    boolean save(Topic topic);               // создаёт или возвращает false, если уже есть
    Topic findByName(String topicName);      // возвращает null, если нет
    Map<String, Topic> findAll();            // возвращает копию или view
    void saveAllFile(Map<String, Topic> topics); // для loadFromFile
}

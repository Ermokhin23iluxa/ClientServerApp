package org.test_task_server.commandLayer.repository;

import org.test_task_server.commandLayer.entity.Topic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTopicRepository implements TopicRepository{
    private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

    @Override
    public boolean save(Topic topic) {
        return topics.putIfAbsent(topic.getTopicName(),topic)==null;
    }

    @Override
    public Topic findByName(String topicName) {
        return topics.get(topicName);
    }

    @Override
    public Map<String, Topic> findAll() {
        return new ConcurrentHashMap<>(topics);
    }

    @Override
    public void saveAllFile(Map<String, Topic> topicsNew) {
        topics.clear();
        topics.putAll(topicsNew);
    }
}

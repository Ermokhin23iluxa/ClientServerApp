package org.test_task_server.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.entity.Topic;
import org.test_task_server.commandLayer.entity.Vote;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TopicService {

    private final Map<String,Topic> topics = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // создание раздела
    public boolean createTopic(String topicName) {
        if (topicName == null || topicName.trim().isEmpty()) {
            return false;
        }
        Topic newTopic = new Topic(topicName);
        return topics.putIfAbsent(topicName, newTopic) == null;
    }


    // возврат раздела по имени
    public Topic getTopic(String topicName){
        return topics.get(topicName);
    }


    public Map<String, Topic> getTopics() {
        return new ConcurrentHashMap<>(topics);
    }


    public boolean addVoteInTopic(String topicName, Vote vote){
        Topic topic = getTopic(topicName);
        if(topic!=null){
            return topic.addVote(vote);
        }
        return false;
    }

    public String getTopicVotes(String topicName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            return "Ошибка: раздел '" + topicName + "' не найден.";
        }
        return topic.superVoteSummary();
    }

    public String getVoteInfo(String topicName, String voteName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            return "Ошибка: раздел '" + topicName + "' не найден.";
        }
        Vote vote = topic.getVote(voteName);
        if (vote == null) {
            return "Ошибка: голосование '" + voteName + "' не найдено в разделе '" + topicName + "'.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Голосование: ").append(vote.getVoteName()).append("\n")
                .append("Описание: ").append(vote.getVoteDescription()).append("\n")
                .append("Создано: ").append(vote.getCreatedBy()).append("\n")
                .append("Варианты:\n");
        for (String option : vote.getOptions()) {
            sb.append("- ")
                    .append(option)
                    .append(": ")
                    .append(vote.calculateVotesForOption(option))
                    .append(" голосов\n");
        }
        return sb.toString();
    }

    public boolean saveToFile(String filename){
        try{
            mapper.writeValue(new File(filename),topics);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadFromFile(String filename){
        try{
            Map<String,Topic> loaded = mapper.readValue(
                    new File(filename),
                    mapper.getTypeFactory().constructMapType(
                            Map.class,String.class, Topic.class
                    )
            );
            topics.clear();
            topics.putAll(loaded);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}

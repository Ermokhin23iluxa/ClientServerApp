package org.test_task_server.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.entity.Topic;
import org.test_task_server.commandLayer.entity.Vote;
import org.test_task_server.commandLayer.repository.TopicRepository;

import java.io.File;
import java.util.Map;

@RequiredArgsConstructor
public class TopicService {

    //private final Map<String,Topic> topics = new ConcurrentHashMap<>();

    private final TopicRepository topicRepository;

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // создание раздела
    public boolean createTopic(String topicName) {
        if (topicName == null || topicName.trim().isEmpty()) return false;
        return topicRepository.save(new Topic(topicName));
    }

    // возврат раздела по имени
    public Topic getTopic(String topicName){
        return topicRepository.findByName(topicName);
    }

    public Map<String, Topic> getTopics() {
        return topicRepository.findAll();
    }

    public boolean addVoteInTopic(String topicName, Vote vote){
        Topic topic = topicRepository.findByName(topicName);
        return topic!=null && topic.addVote(vote);
    }

    public String getTopicVotes(String topicName) {
        Topic topic = topicRepository.findByName(topicName);
        if (topic == null) {
            return "Ошибка: раздел '" + topicName + "' не найден.";
        }
        return topic.superVoteSummary();
    }

    public String getVoteInfo(String topicName, String voteName) {
        Topic topic = topicRepository.findByName(topicName);
        if (topic == null) {
            return "Ошибка: раздел '" + topicName + "' не найден.";
        }
        return topic.getVoteInfo(voteName);

    }

    public boolean saveToFile(String filename){
        try{
            mapper.writeValue(new File(filename),topicRepository.findAll());
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
            topicRepository.saveAllFile(loaded);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}

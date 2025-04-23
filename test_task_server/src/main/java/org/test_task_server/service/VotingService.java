package org.test_task_server.service;

import lombok.RequiredArgsConstructor;
import org.test_task_server.commandLayer.entity.Topic;
import org.test_task_server.commandLayer.entity.Vote;

@RequiredArgsConstructor
public class VotingService {
    private final TopicService topicService;


    public boolean deleteVote(String topicName, String voteName, String currentUser) {
        Topic topic = topicService.getTopic(topicName);
        if (topic == null) {
            return false;
        }
        Vote vote = topic.getVote(voteName);
        if (vote == null) {
            return false;
        }
        if (!vote.getCreatedBy().equals(currentUser)) {
            return false;
        }
        return topic.getVotes().remove(voteName) != null;
    }

    public boolean vote(String topicName, String voteName, String username, String chosenOption) {
        Topic topic = topicService.getTopic(topicName);
        if (topic == null) return false;
        Vote vote = topic.getVote(voteName);
        if (vote == null) return false;
        // Добавляем голос. Если пользователь уже голосовал, метод addVote вернет false.
        return vote.addVote(chosenOption, username);
    }
}

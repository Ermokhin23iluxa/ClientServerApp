package org.test_task_server.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

public class TopicServiceTest {

    @InjectMocks
    private TopicService topicService;


    @Test
    void createTopic_succeedsOnNewName(){
        boolean created = topicService.createTopic("sports");
        assertTrue(created,"Первое создание раздела должно вернуть true");

        assertFalse(topicService.createTopic("sports"),
                "Повторное создание раздела с тем же именем должно вернуть false");
    }

    @Test
    void getTopicVotes_emptyIfNoVotes() {
        topicService.createTopic("tech");
        String summary = topicService.getTopicVotes("tech");
        assertEquals("Нет голосований.", summary.trim());
    }
    @Test
    void getTopic_returnFalse_WhenTopicUnExists(){
        String topicName="topic";
        Assertions.assertNull(topicService.getTopic(topicName));
    }
    @Test
    void getVoteInfo_returnFalse_WhenDoesNotVotes(){

    }


}

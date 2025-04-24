package org.test_task_server.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TopicServiceTest {
    private TopicService topicService;

    @BeforeEach
    void setUp(){
        topicService=new TopicService();
    }
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

}

package org.test_task_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test_task_server.commandLayer.entity.Topic;
import org.test_task_server.commandLayer.entity.Vote;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingServiceTest {

    @Mock
    TopicService topicService;

    @InjectMocks
    VotingService votingService;

    // VOTE
    @Test
    void vote_ReturnFalse_WhenTopicNotExists(){
        String topicName = "topicName";
        when(topicService.getTopic(topicName)).thenReturn(null);

        boolean response = votingService.vote(topicName, "voteName", "userName", "1");

        assertFalse(response);
        verify(topicService, times(1)).getTopic(topicName);
        verifyNoMoreInteractions(topicService);
    }
    @Test
    void vote_ReturnFalse_WhenVoteNotExistsInTopic(){
        String topicName = "topicName";
        String voteName = "voteName";

        // Тема существует, но в ней нет нужного голосования
        Topic mockTopic = mock(Topic.class);
        when(topicService.getTopic(topicName)).thenReturn(mockTopic);
        when(mockTopic.getVote(voteName)).thenReturn(null);

        boolean response = votingService.vote(topicName,voteName, "userName", "1");

        assertFalse(response, "Должно вернуть false, т.к. голосования 'voteName' нет в разделе 'topicName'");

        // Проверяем интеракции
        InOrder inOrder = inOrder(topicService,mockTopic);
        inOrder.verify(topicService).getTopic(topicName);
        inOrder.verify(mockTopic).getVote(voteName);
        verifyNoMoreInteractions(topicService,mockTopic);
    }
    @Test
    void vote_ReturnFalse_WhenOptionInvalid(){
        String topicName = "topicName";
        String voteName = "voteName";
        String userName = "userName";
        String option = "option1";

        // Тема существует, голосование существует

        Topic mockTopic = mock(Topic.class);
        Vote mockVote = mock(Vote.class);
        when(topicService.getTopic(topicName)).thenReturn(mockTopic);
        when(mockTopic.getVote(voteName)).thenReturn(mockVote);
        when(mockVote.addVote(option,userName)).thenReturn(false);

        boolean response = votingService.vote(topicName,voteName, userName, option);

        assertFalse(response, "Должно вернуть false,так как голосуем за несуществующую опцию");

        // Проверяем интеракции
        InOrder inOrder = inOrder(topicService,mockTopic,mockVote);
        inOrder.verify(topicService).getTopic(topicName);
        inOrder.verify(mockTopic).getVote(voteName);
        inOrder.verify(mockVote).addVote(option,userName);
        verifyNoMoreInteractions(topicService,mockTopic,mockVote);
    }
    @Test
    void vote_ReturnTrue_WhenEveryThingValid(){
        String topicName = "topicName";
        String voteName = "voteName";
        String userName = "userName";
        String option = "option1";

        // Тема существует, голосование существует, опция существует

        Topic mockTopic = mock(Topic.class);
        Vote mockVote = mock(Vote.class);

        when(topicService.getTopic(topicName)).thenReturn(mockTopic);
        when(mockTopic.getVote(voteName)).thenReturn(mockVote);
        when(mockVote.addVote(option,userName)).thenReturn(true);

        boolean response = votingService.vote(topicName,voteName, userName, option);

        assertTrue(response, "Должно вернуть true,так как голосуем за существующую опцию");

        // Проверяем интеракции
        InOrder inOrder = inOrder(topicService,mockTopic,mockVote);
        inOrder.verify(topicService).getTopic(topicName);
        inOrder.verify(mockTopic).getVote(voteName);
        inOrder.verify(mockVote).addVote(option,userName);
        verifyNoMoreInteractions(topicService,mockTopic,mockVote);
    }
    @Test
    void vote_ReturnFalse_WhenUserVotesTwice(){
        String topicName = "topic";
        String voteName = "vote";
        String userName = "user";
        String option = "option1";

        // Тема существует, голосование существует, опция существует

        Topic mockTopic = mock(Topic.class);
        Vote mockVote = mock(Vote.class);

        when(topicService.getTopic(topicName)).thenReturn(mockTopic);
        when(mockTopic.getVote(voteName)).thenReturn(mockVote);
        when(mockVote.addVote(option,userName))
                .thenReturn(true)
                .thenReturn(false);

        boolean response = votingService.vote(topicName,voteName, userName, option);
        boolean duplicateResponse = votingService.vote(topicName,voteName, userName, option);

        assertTrue(response, "Должно вернуть true,так как голосуем впервые за опцию");
        assertFalse(duplicateResponse, "Должно вернуть false,так как голосуем дважды за опцию");

        // Проверяем интеракции
        verify(mockVote,times(2)).addVote(option,userName);
        verifyNoMoreInteractions(topicService,mockTopic,mockVote);
    }

    // DELETE

    @Test
    void deleteVote_ReturnFalse_WhenTopicNotExists() {
        String topicName = "topic";

        when(topicService.getTopic(topicName)).thenReturn(null);

        boolean result = votingService.deleteVote(topicName, "vote", "user");

        assertFalse(result,"Далжно быть false, так как топика не существует");

        verify(topicService).getTopic(topicName);
        verifyNoMoreInteractions(topicService);

    }

    @Test
    void deleteVote_ReturnFalse_WhenVoteNotExists() {
        String topicName = "topic";
        String voteName = "vote";

        // Тема существует, но в ней нет нужного голосования
        Topic mockTopic = mock(Topic.class);
        when(topicService.getTopic(topicName)).thenReturn(mockTopic);
        when(mockTopic.getVote(voteName)).thenReturn(null);

        boolean response = votingService.deleteVote(topicName,voteName, "user");

        assertFalse(response, "Должно вернуть false, т.к. голосования 'voteName' нет в разделе 'topicName'");

        // Проверяем интеракции
        InOrder inOrder = inOrder(topicService,mockTopic);
        inOrder.verify(topicService).getTopic(topicName);
        inOrder.verify(mockTopic).getVote(voteName);
        verifyNoMoreInteractions(topicService,mockTopic);
    }
    @Test
    void deleteVote_ReturnFalse_WhenNotAuthor() {
        String topicName = "topic";
        String voteName = "vote";
        String userName = "Alice";

        // Тема существует, голосование существует, опция существует

        Topic mockTopic = mock(Topic.class);
        Vote mockVote = mock(Vote.class);

        when(topicService.getTopic(topicName)).thenReturn(mockTopic);
        when(mockTopic.getVote(voteName)).thenReturn(mockVote);
        when(mockVote.getCreatedBy()).thenReturn(userName);

        boolean response = votingService.deleteVote(topicName,voteName, "NotAlice");

        assertFalse(response, "Должно вернуть false,так как удаляет голосвание не автор");

        // Проверяем интеракции
        InOrder inOrder = inOrder(topicService,mockTopic,mockVote);
        inOrder.verify(topicService).getTopic(topicName);
        inOrder.verify(mockTopic).getVote(voteName);
        inOrder.verify(mockVote).getCreatedBy();
        verifyNoMoreInteractions(topicService,mockTopic,mockVote);
    }
    @Test
    void deleteVote_ReturnTrue_WhenAuthor() {
        String topicName = "topic";
        String voteName = "vote";
        String userName = "Alice";

        // Тема существует, голосование существует, опция существует

        Topic mockTopic = mock(Topic.class);
        Vote mockVote = mock(Vote.class);
        ConcurrentHashMap<String, Vote> mockMap = mock(ConcurrentHashMap.class);

        when(topicService.getTopic(topicName)).thenReturn(mockTopic);
        when(mockTopic.getVote(voteName)).thenReturn(mockVote);
        when(mockVote.getCreatedBy()).thenReturn(userName);
        when(mockTopic.getVotes()).thenReturn(mockMap);
        when(mockMap.remove(voteName)).thenReturn(mockVote);

        boolean result = votingService.deleteVote(topicName, voteName, userName);
        assertTrue(result);

        InOrder inOrder = inOrder(topicService, mockTopic, mockVote,mockMap);
        inOrder.verify(topicService).getTopic(topicName);
        inOrder.verify(mockTopic).getVote(voteName);
        inOrder.verify(mockVote).getCreatedBy();
        inOrder.verify(mockTopic).getVotes();
        inOrder.verify(mockMap).remove(voteName);

        verifyNoMoreInteractions(topicService, mockTopic, mockVote, mockMap);
    }


}
package org.test_task_server.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test_task_server.commandLayer.entity.Topic;
import org.test_task_server.commandLayer.entity.Vote;
import org.test_task_server.commandLayer.repository.TopicRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {
    @Mock
    TopicRepository repository;
    @InjectMocks
    private TopicService topicService;

    @Test
    void createTopic_ReturnFalse_WhenNameNullOrEmpty(){
        String topicNameEmpty = " ";

        assertFalse(topicService.createTopic(null));
        assertFalse(topicService.createTopic(topicNameEmpty));
        verifyNoInteractions(repository);

    }

    @Test
    void createTopic_CallsRepositorySave() {
        String topicName = "topicName";

        when(repository.save(any())).thenReturn(true);

        boolean createOk = topicService.createTopic(topicName);

        assertTrue(createOk);
        verify(repository).save(argThat(t -> t.getTopicName().equals(topicName)));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getTopicVotes_Error_WhenNotFound(){
        when(repository.findByName("noTopic")).thenReturn(null);
        String result = topicService.getTopicVotes("noTopic");
        assertTrue(result.contains("не найден"));
        verify(repository).findByName("noTopic");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void addVoteInTopic_DelegatesToTopic() {
        Topic mockTopic = mock(Topic.class);
        String topicName = "topic";
        String voteName = "vote";
        String voteDescription = "description";
        String createdBy = "user";
        List<String> options = List.of("o1");

        Vote vote = new Vote(voteName,voteDescription,createdBy, options);

        when(repository.findByName(topicName)).thenReturn(mockTopic);
        when(mockTopic.addVote(vote)).thenReturn(true);

        assertTrue(topicService.addVoteInTopic(topicName, vote));

        InOrder io = inOrder(repository, mockTopic);
        io.verify(repository).findByName(topicName);
        io.verify(mockTopic).addVote(vote);
        verifyNoMoreInteractions(repository, mockTopic);
    }

    @Test
    void getVoteInfo_Error_WhenTopicMissing() {

        when(repository.findByName("t")).thenReturn(null);
        String res = topicService.getVoteInfo("t","v");

        assertTrue(res.contains("не найден"));
        verify(repository).findByName("t");
        verifyNoMoreInteractions(repository);
    }



    @Test
    void getVoteInfo_DelegatesToTopic() {
        Topic mockTopic = mock(Topic.class);
        when(repository.findByName("t")).thenReturn(mockTopic);
        when(mockTopic.getVoteInfo("v"))
                .thenReturn("INFO");
        String res = topicService.getVoteInfo("t","v");
        assertEquals("INFO", res);

        InOrder io = inOrder(repository, mockTopic);
        io.verify(repository).findByName("t");
        io.verify(mockTopic).getVoteInfo("v");
        verifyNoMoreInteractions(repository, mockTopic);
    }

    @Test
    void getTopics_ReturnsRepoFindAll() {
        Map<String, Topic> data = Map.of("a", new Topic("a"));
        when(repository.findAll()).thenReturn(data);
        Map<String, Topic> all = topicService.getTopics();
        assertSame(data, all);

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void saveToFile_ReturnsFalse_OnException() throws Exception {
        // 1. Подшпионим сервис
        TopicService spyService = spy(topicService);

        // 2. Создадим мок ObjectMapper, который бросает исключение на writeValue
        ObjectMapper badMapper = mock(ObjectMapper.class);
        doThrow(new RuntimeException("disk error"))
                .when(badMapper).writeValue(any(File.class), any());

        // 3. Подменим приватное поле mapper рефлексией
        var field = TopicService.class.getDeclaredField("mapper");
        field.setAccessible(true);
        field.set(spyService, badMapper);

        // 4. Вызов должен вернуть false
        boolean result = spyService.saveToFile("anyfile.json");
        assertFalse(result, "При исключении внутри ObjectMapper.save должно вернуться false");
    }

    @Test
    void loadFromFile_CallsRepoSaveAllFile(@TempDir Path tmpDir) throws Exception {
        // 1. Подготовим реальный ObjectMapper для генерации JSON
        ObjectMapper realMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        // 2. Создадим тестовый файл JSON со словарём одного раздела
        Map<String, Topic> sample = Map.of("t1", new Topic("t1"));
        File jsonFile = tmpDir.resolve("test.json").toFile();
        realMapper.writeValue(jsonFile, sample);

        // 3. Заменим в сервисе его mapper на наш реальный
        TopicService spyService = spy(topicService);
        var field = TopicService.class.getDeclaredField("mapper");
        field.setAccessible(true);
        field.set(spyService, realMapper);

        // 4. Вызов loadFromFile
        boolean ok = spyService.loadFromFile(jsonFile.getAbsolutePath());
        assertTrue(ok, "При корректном JSON метод должен вернуть true");

        // 5. Проверяем, что репозиторий сохранил данные из файла
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Topic>> captor =
                ArgumentCaptor.forClass(Map.class);

        verify(repository).saveAllFile(captor.capture());
        Map<String, Topic> passed = captor.getValue();
        assertTrue(passed.containsKey("t1"), "Репозиторий должен получить ключ 't1'");
        verifyNoMoreInteractions(repository);
    }


//    @Test
//    void createTopic_succeedsOnNewName(){
//        boolean created = topicService.createTopic("sports");
//        assertTrue(created,"Первое создание раздела должно вернуть true");
//
//        assertFalse(topicService.createTopic("sports"),
//                "Повторное создание раздела с тем же именем должно вернуть false");
//    }
//
//    @Test
//    void getTopicVotes_emptyIfNoVotes() {
//        topicService.createTopic("tech");
//        String summary = topicService.getTopicVotes("tech");
//        assertEquals("Нет голосований.", summary.trim());
//    }
//    @Test
//    void getTopic_returnFalse_WhenTopicUnExists(){
//        String topicName="topic";
//        Assertions.assertNull(topicService.getTopic(topicName));
//    }
//    @Test
//    void getVoteInfo_returnFalse_WhenDoesNotVotes(){
//
//    }


}

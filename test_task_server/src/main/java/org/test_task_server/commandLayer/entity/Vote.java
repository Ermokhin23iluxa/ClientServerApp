package org.test_task_server.commandLayer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@NoArgsConstructor
@Setter
public class Vote {
    private  String voteName;
    private  String voteDescription;
    private  String createdBy;  // создатель голосования
    private  List<String> options;
    // Для каждой опции хранится набор имён пользователей, которые проголосовали за неё.
    private final Map<String, HashSet<String>> results = new ConcurrentHashMap<>();

    public Vote(String voteName, String voteDescription, String createdBy, List<String> options) {
        this.voteName = voteName;
        this.voteDescription = voteDescription;
        this.createdBy = createdBy;
        this.options = options;
        // Инициализация результатов: для каждой опции создаём пустой набор.
        for (String option : options) {
            results.put(option, new HashSet<>());
        }
    }


    public boolean addVote(String option, String username) {
        if (!results.containsKey(option)) {
            return false;  // Опция отсутствует
        }
        // Проверяем, не голосовал ли уже пользователь (можно проверить во всех наборах)
        for (HashSet<String> voters : results.values()) {
            if (voters.contains(username)) {
                return false;  // Пользователь уже отдал голос
            }
        }
        results.get(option).add(username);
        return true;
    }

    private String calculateResultsSummary() {
        StringBuilder sb = new StringBuilder();
        for (String option : options) {
            int count = results.get(option).size();
            sb.append(option).append(": Голосов:  ").append(count).append("\n");
        }
        return sb.toString();
    }
    public int calculateVotesForOption(String option){
        HashSet<String> voters = results.get(option);
        return voters == null ? 0 : voters.size();
    }

    @Override
    public String toString() {
        return "Голосование: " + voteName + "\n" +
                "Описание: " + voteDescription + "\n" +
                "Создано: " + createdBy + "\n" +
                "Результаты:\n" + calculateResultsSummary();
    }
}

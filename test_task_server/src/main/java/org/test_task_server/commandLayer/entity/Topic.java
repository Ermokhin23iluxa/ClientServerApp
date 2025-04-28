package org.test_task_server.commandLayer.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;


@Getter
@NoArgsConstructor
@Setter
public class Topic {
    private  String topicName;

    private final ConcurrentHashMap<String, Vote> votes = new ConcurrentHashMap<>();

    public Topic(String topicName) {
        this.topicName = topicName;
    }

    public boolean addVote(Vote vote){
        if (vote == null || vote.getVoteName() == null || vote.getVoteName().trim().isEmpty()) {
            return false;
        }
        return votes.putIfAbsent(vote.getVoteName(), vote) == null;
    }

    public String superVoteSummary(){
        if(votes.isEmpty()){
            return "Нет голосований.";
        }
        StringBuilder sb = new StringBuilder();
        for(Vote vote:votes.values()){
            sb.append(vote.getVoteName())
                    .append(" (вариантов: ")
                    .append(vote.getOptions().size())
                    .append(")\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return topicName + " (голосований: " + votes.size() + ")";
    }

    public Vote getVote(String voteName){
        return votes.get(voteName);
    }

    public String getVoteInfo(String voteName) {
        Vote vote = this.getVote(voteName);
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
}

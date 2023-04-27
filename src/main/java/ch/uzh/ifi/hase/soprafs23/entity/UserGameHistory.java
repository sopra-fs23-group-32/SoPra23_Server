package ch.uzh.ifi.hase.soprafs23.entity;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */
@Entity
public class UserGameHistory{
    @Id
    private Long userId;

    private Long gameId;
    private int gameScore;

    @ElementCollection
    private final List<String> answerList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userStatistics_userId")
    private UserStatistics userStatistics;

    public Long getUserId() {return userId;}
    public void setUserId(long userId) {this.userId = userId;}

    public Long getGameId() {return gameId;}
    public void setGameId(long gameId) {this.gameId = gameId;}

    public int getGameScore() {return gameScore;}
    public void setGameScore(int gameScore) {this.gameScore = gameScore;}

    public Iterator<String> getAnswerList() {return answerList.iterator();}
    public void addAnswer(String answer) {answerList.add(answer);}

    public void setUserStatistics(UserStatistics userStatistics) {
        this.userStatistics = userStatistics;
    }
}

package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */

public class Player {

    private Long userId;
    private String playerName;
    private int score;
    private final List<String> answerList = new ArrayList<>();

    public Player(long userId, String name) {
        this.userId = userId;
        this.playerName = name;
        this.score = 0;
    }

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getPlayerName() {return playerName;}
    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public int getScore() {return score;}
    public void addScore(int score) {this.score += score;}

    public void addAnswer(String newAnswer) {answerList.add(newAnswer);}
    public Iterator<String> getAnswerList() {return answerList.iterator();}
}

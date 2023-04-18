package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */
public class UserGameHistory{
    private long gameId;
    private int gameScore;
    private final List<String> answerList = new ArrayList<>();

    public UserGameHistory(long gameId, int score) {
        this.gameId = gameId;
        this.gameScore = score;
    }

    public long getGameId() {return gameId;}
    public void setGameId(long gameId) {this.gameId = gameId;}

    public int getGameScore() {return gameScore;}
    public void setGameScore(int gameScore) {this.gameScore = gameScore;}

    public Iterator<String> getAnswerList() {return answerList.iterator();}
    public void addAnswer(String answer) {answerList.add(answer);}
}

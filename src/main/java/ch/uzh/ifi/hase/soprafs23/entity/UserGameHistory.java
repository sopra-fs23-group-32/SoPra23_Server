package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */
public class UserGameHistory{
    private final long gameId;
    private final int gameScore;
    private final GameInfo gameInfo;
    private final List<String> answerList = new ArrayList<>();

    public UserGameHistory(long gameId, int score, GameInfo gameInfo) {
        this.gameId = gameId;
        this.gameScore = score;
        this.gameInfo = gameInfo;
    }

    public long getGameId() {return gameId;}

    public int getGameScore() {return gameScore;}

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void addLabel(String answer) {
        answerList.add(answer);
    }
    public Iterator<String> getAnswerList() {
        return answerList.iterator();
    }
}

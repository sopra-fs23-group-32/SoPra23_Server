package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class GameHistoryGetDTO {
    private long gameId;
    private int gameScore;
    private float correctRate;

    public long getGameId() {return gameId;}
    public void setGameId(long gameId) {this.gameId = gameId;}

    public int getGameScore() {return gameScore;}
    public void setGameScore(int gameScore) {this.gameScore = gameScore;}

    public float getCorrectRate() {return correctRate;}
    public void setCorrectRate(float rate) {this.correctRate = rate;}
}

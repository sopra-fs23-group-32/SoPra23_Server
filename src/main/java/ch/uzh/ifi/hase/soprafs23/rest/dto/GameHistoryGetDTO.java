package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class GameHistoryGetDTO {
    private long gameId;
    private int gameScore;

    public long getGameId() {return gameId;}
    public void setGameId(long gameId) {this.gameId = gameId;}

    public int getGameScore() {return gameScore;}
    public void setGameScore(int gameScore) {this.gameScore = gameScore;}
}

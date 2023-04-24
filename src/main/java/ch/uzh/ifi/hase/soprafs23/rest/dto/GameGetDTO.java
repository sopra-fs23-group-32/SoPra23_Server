package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class GameGetDTO {

    private Long gameId;
    private int currentRound;
    private String currentAnswer;

    public Long getGameId() {return gameId;}
    public void setGameId(Long gameId) {this.gameId = gameId;}

    public int getCurrentRound() {return currentRound;}
    public void setCurrentRound(int currentRound) {this.currentRound = currentRound;}

    public String getCurrentAnswer() {return currentAnswer;}
    public void setCurrentAnswer(String currentAnswer) {this.currentAnswer = currentAnswer;}
}

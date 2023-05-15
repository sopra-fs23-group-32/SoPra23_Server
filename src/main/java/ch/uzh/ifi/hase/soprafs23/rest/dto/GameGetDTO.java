package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class GameGetDTO {

    private Long gameId;
    private int currentRound;
    private int totalRounds;
    private int countdownTime;
    private String category;
    private String currentAnswer;


    public Long getGameId() {return gameId;}
    public void setGameId(Long gameId) {this.gameId = gameId;}

    public int getCurrentRound() {return currentRound;}
    public void setCurrentRound(int currentRound) {this.currentRound = currentRound;}

    public int getTotalRounds() {return totalRounds;}
    public void setTotalRounds(int totalRounds) {this.totalRounds = totalRounds;}

    public String getCurrentAnswer() {return currentAnswer;}
    public void setCurrentAnswer(String currentAnswer) {this.currentAnswer = currentAnswer;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}


    public int getCountdownTime() {return countdownTime;}
    public void setCountdownTime(int countdownTime) {this.countdownTime = countdownTime;}
}

package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

public class GameGetDTO {

    private Long gameId;
    private CityCategory category;
    private int totalRounds;
    private int countdownTime;

    private int currentRound;
    private String currentAnswer;
    private int playerNum;
    private String hostname;

    public Long getGameId() {return gameId;}
    public void setGameId(Long gameId) {this.gameId = gameId;}

    public CityCategory getCategory() {return category;}
    public void setCategory(CityCategory category) {this.category = category;}

    public int getTotalRounds() {return totalRounds;}
    public void setTotalRounds(int totalRounds) {this.totalRounds = totalRounds;}

    public int getCountdownTime() {return countdownTime;}
    public void setCountdownTime(int countdownTime) {this.countdownTime = countdownTime;}

    public int getCurrentRound() {return currentRound;}
    public void setCurrentRound(int currentRound) {this.currentRound = currentRound;}

    public String getCurrentAnswer() {return currentAnswer;}
    public void setCurrentAnswer(String currentAnswer) {this.currentAnswer = currentAnswer;}

    public int getPlayerNum() {return playerNum;}
    public void setPlayerNum(int playerNum) {this.playerNum = playerNum;}

    public void setHostname(String hostname) {this.hostname = hostname;}
    public String getHostname() {return hostname;}
}

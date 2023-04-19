package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;


public class GamePostDTO {

    private Long gameId;
    private CityCategory category;
    private int totalRounds;
    private int countdownTime;

    public Long getGameId() {return gameId;}
    public void setGameId(Long gameId) {this.gameId = gameId;}

    public CityCategory getCategory() {return category;}
    public void setCategory(CityCategory category) {this.category = category;}

    public int getTotalRounds() {return totalRounds;}
    public void setTotalRounds(int totalRounds) {this.totalRounds = totalRounds;}

    public int getCountdownTime() {return countdownTime;}
    public void setCountdownTime(int countdownTime) {this.countdownTime = countdownTime;}
}

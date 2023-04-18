package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import java.util.Date;

public class GameInfoGetDTO {

    private long gameId;
    private CityCategory category;
    private Date gameDate;
    private int gameRounds;
    private int playerNum;

    public Long getGameId() {return gameId;}
    public void setGameId(Long gameId) {this.gameId = gameId;}

    public CityCategory getCategory() {return category;}
    public void setCategory(CityCategory category) {this.category = category;}

    public Date getGameDate() {return gameDate;}
    public void setGameDate(Date gameDate) {this.gameDate = gameDate;}

    public int getGameRounds() {return gameRounds;}
    public void setGameRounds(int gameRounds) {this.gameRounds = gameRounds;}

    public int getPlayerNum() {return playerNum;}
    public void setPlayerNum(int playerNum) {this.playerNum = playerNum;}

}

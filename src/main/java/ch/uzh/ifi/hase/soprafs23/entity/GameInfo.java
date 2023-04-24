package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */
@Entity
@Table(name = "GAMEINFO")
public class GameInfo{
    @Id
    private Long gameId;

    private CityCategory category;
    private Date gameDate;
    private int gameRounds;
    private int playerNum;
    @ElementCollection
    private List<String> labelList = new ArrayList<>();

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

    public void addLabel(String label) {labelList.add(label);}
    public Iterator<String> getLabelList() {return labelList.iterator();}
}

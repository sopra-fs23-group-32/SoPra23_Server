package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */
@Entity
@Table(name = "GAMEINFO")
public class GameInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long gameId;
    @Column(nullable = false)
    private CityCategory category;
    @Column(nullable = false)
    private Date gameTime;
    @Column(nullable = false)
    private int gameRounds;
    @Column(nullable = false)
    private int playerNum;

    @Transient
    private List<String> labelList = new ArrayList<>();

    public Long getGameId() {return gameId;}

    public CityCategory getCategory() {return category;}

    public Date getGameTime() {return gameTime;}

    public int getGameRounds() {return gameRounds;}

    public int getPlayerNum() {return playerNum;}

    public void addLabel(String label) {
        labelList.add(label);
    }
    public Iterator<String> getLabelList() {
        return labelList.iterator();
    }
}

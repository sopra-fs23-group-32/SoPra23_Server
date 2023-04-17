package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "SINGLEMODEGAMES")
public class SingleModeGame implements Serializable {
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
    private int totalRounds;
    @Column(nullable = false)
    private int countdownTime;
    @Column(nullable = false)
    private String solutionCityName;
    @ElementCollection
    private List<String> cityOptions;
    @Column(nullable = false)
    private int  player_id;
    @Column(nullable = false)
    private boolean gameEnded;
    @Column(nullable = false)
    private int playerScore;
    @Column(nullable = false)
    private int currentRound;
    private String imageUrl;

    public Long getGameId() {
        return gameId;
    }

    public CityCategory getCategory() {
        return category;
    }

    public Date getGameTime() {
        return gameTime;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(int countdownTime) {
        this.countdownTime = countdownTime;
    }

    public String getSolutionCityName() {
        return solutionCityName;
    }

    public void setSolutionCityName(String solutionCityName) {
        this.solutionCityName = solutionCityName;
    }

    public List<String> getCityOptions() {
        return cityOptions;
    }

    public void setCityOptions(List<String> cityOptions) {
        this.cityOptions = cityOptions;
    }

    public int getPlayer() {
        return player_id;
    }

    public void setPlayer(int player_id) {
        this.player_id = player_id;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

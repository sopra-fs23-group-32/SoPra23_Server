package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import javax.persistence.*;
import java.util.*;

/**
 * @author Zilong Deng
 */
@Entity
public class UserStatistics {
    @Id
    private Long userId;

    private long totalScore;
    private long totalGameNum;

    @ElementCollection
    private Map<CityCategory, Long> specificScore;
    @ElementCollection
    private Map<CityCategory, Long> specificGameNum;

    @OneToMany(mappedBy = "userStatistics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGameHistory> gameHistories = new ArrayList<>();

    public void initUserStatistics() {
        totalScore = 0;
        totalGameNum = 0;
        // Initialize the HashMap
        specificScore = new HashMap<>() {{
            put(CityCategory.EUROPE, (long)0);
            put(CityCategory.ASIA, (long)0);
            put(CityCategory.NORTH_AMERICA, (long)0);
            put(CityCategory.SOUTH_AMERICA, (long)0);
            put(CityCategory.AFRICA, (long)0);
            put(CityCategory.OCEANIA, (long)0);
        }};
        specificGameNum = new HashMap<>() {{
            put(CityCategory.EUROPE, (long)0);
            put(CityCategory.ASIA, (long)0);
            put(CityCategory.NORTH_AMERICA, (long)0);
            put(CityCategory.SOUTH_AMERICA, (long)0);
            put(CityCategory.AFRICA, (long)0);
            put(CityCategory.OCEANIA, (long)0);
        }};
    }

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public long getTotalScore() {return totalScore;}
    public long getSpecificScore(CityCategory category) {return specificScore.get(category);}

    public void updateScore(long score, CityCategory category) {
        totalScore += score;
        long tempScore = specificScore.get(category);
        specificScore.put(category, tempScore + score);
    }

    public long getTotalGameNum() {return totalGameNum;}
    public long getSpecificGameNum(CityCategory category) {return specificGameNum.get(category);}

    public void updateGameNum(CityCategory category) {
        this.totalGameNum += 1;
        long tempGameNum = specificGameNum.get(category);
        specificGameNum.put(category, tempGameNum + 1);
    }

    public void addGameHistory(UserGameHistory history) {
        gameHistories.add(history);
    }
    public Iterator<UserGameHistory> getGameHistoryList(){
        return gameHistories.iterator();
    }
}

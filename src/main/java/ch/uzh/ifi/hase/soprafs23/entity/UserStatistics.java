package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import javax.persistence.*;
import java.util.*;

/**
 * @author Zilong Deng
 */
@Entity
@Table(name = "STATISTICS")
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
    
    //****CHANGE SAID 24.05.2023 ***************** */
    public void initUserStatistics() {
        totalScore = 0;
        totalGameNum = 0;
    
        specificScore = new HashMap<>();
        for (CityCategory category : CityCategory.values()) {
            specificScore.put(category, (long) 0);
        }
    
        specificGameNum = new HashMap<>();
        for (CityCategory category : CityCategory.values()) {
            specificGameNum.put(category, (long) 0);
        }
    }
    //****CHANGE SAID 24.05.2023 ***************** */


    

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
    public int getLenGameHistories() {return gameHistories.size();}
}

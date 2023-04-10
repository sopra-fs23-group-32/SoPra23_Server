package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import java.util.*;

/**
 * @author Zilong Deng
 */
public class UserStatistics {
    private long totalScore;
    private long totalGameNum;
    private final Map<CityCategory, Long> specificScore;
    private final List<UserGameHistory> gameHistories = new ArrayList<>();

    public UserStatistics() {
        specificScore = new HashMap<>() {{
            put(CityCategory.EUROPE, (long)0);
            put(CityCategory.ASIA, (long)0);
            put(CityCategory.NORTH_AMERICA, (long)0);
        }};
    }

    public long getTotalScore() {return totalScore;}
    public long getSpecificScore(CityCategory category) {
        return specificScore.get(category);
    }
    public void updateScore(long score, CityCategory category) {
        totalScore += score;
        long tempScore = specificScore.get(category);
        specificScore.put(category, tempScore+score);
    }

    public long getTotalGameNum() {return totalGameNum;}
    public void addTotalGameNum() {this.totalGameNum += 1;}

    public void addGameHistory(UserGameHistory history) {
        gameHistories.add(history);
    }

    public Iterator<UserGameHistory> getGameHistoryList(){
        return gameHistories.iterator();
    }
}

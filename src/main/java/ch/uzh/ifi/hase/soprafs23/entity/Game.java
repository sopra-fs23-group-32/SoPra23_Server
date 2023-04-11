package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Zilong Deng
 */
public class Game {
    private int currentRound;
    private final int totalRounds;
    private final int countdownTime;
    public CityBase cityDB;
    private final List<Player> playerList;

    public Game(int rounds, int countdown, CityBase cityDB) {
        this.totalRounds = rounds;
        this.currentRound = 0;
        this.countdownTime = countdown;
        this.cityDB = cityDB;
        playerList = new ArrayList<>();
    }

    public int getCurrentRound() {return currentRound;}
    public int getTotalRounds() {return totalRounds;}

    public int getCountdownTime() {return countdownTime;}

    public void addPlayer(User userAsPlayer) {
        playerList.add(new Player(
            userAsPlayer.getUserId(), userAsPlayer.getUsername()
        ));
    }

    public void showRanking() {
//        playerList.sort((o1, o2) -> o1.getScore() - o2.getScore());
        playerList.sort(Comparator.comparingInt(Player::getScore));
        // temp implementation
        for(Player player:playerList) {
            System.out.println(player.getPlayerName() + player.getScore());
        }
    }
}

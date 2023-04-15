package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * @author Zilong Deng
 */
public class Game {
    private int currentRound;
    private final int totalRounds;
    private final int countdownTime;
    public CityBase cityDB;
    private final List<Player> playerList;
    private boolean gameEnded;
    private Map<Player, Integer> playerScores;
    private List<Round> rounds;

    public Game(int rounds, int countdown, CityBase cityDB) {
        this.totalRounds = rounds;
        this.currentRound = 0;
        this.countdownTime = countdown;
        this.cityDB = cityDB;
        this.playerList = new ArrayList<>();
        this.rounds = new ArrayList<>();
        this.gameEnded = false;
        generateNextRound();
    }

    public void generateNextRound() {
        currentRound = rounds.size() + 1;
        if (currentRound > totalRounds) {
            return;
        }
        Round round = new Round(this, currentRound, cityDB, countdownTime);
        rounds.add(round);
    }

    public int getCurrentRound() {return currentRound;}
    public int getTotalRounds() {return totalRounds;}

    public int getCountdownTime() {return countdownTime;}
    public boolean isGameEnded() {return gameEnded;}
    public void setGameEnded(boolean gameEnded) {this.gameEnded = gameEnded;}

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

    public Player getPlayerById(Long playerId) {
        for (Player player: playerList) {
            if (player.getUserId() == playerId) {
                return player;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("User with ID %d was not found!\n", playerId));
    }

    public void submitAnswers(List<Answer> answers) {
        int currentRound = getCurrentRound();
        Round currentRoundObj = rounds.get(currentRound - 1);
        for (Answer answer : answers) {
            currentRoundObj.submitAnswer(answer);
        }
    }
}

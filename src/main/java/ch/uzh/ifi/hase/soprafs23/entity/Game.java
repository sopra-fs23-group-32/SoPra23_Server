package ch.uzh.ifi.hase.soprafs23.entity;

import org.springframework.beans.factory.annotation.Autowired;
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
    public City city;
    private final List<Player> playerList;
    private boolean gameEnded;
    private Map<Player, Integer> playerScores;
    private List<Round> rounds;
    private ArrayList<String> cityoptions;
    private String imageUrl;

    /**
     * Constructor for the Game class.
     *
     * @param rounds     The total number of rounds in the game.
     * @param countdown  The time limit for each round in seconds.
     * @param city       The right city for the game.
     * @param cityoptions       The 5 city options including the right city.
     *
     */
<<<<<<< HEAD
    public Game(int rounds, int countdown, City city) {
=======
    public Game(int rounds, int countdown, List<String> city_names) {
>>>>>>> master
        this.totalRounds = rounds;
        this.currentRound = 0;
        this.countdownTime = countdown;
        this.playerList = new ArrayList<>();
        this.rounds = new ArrayList<>();
        this.gameEnded = false;
<<<<<<< HEAD
        this.cityoptions=city.getCityoptions();
=======
        this.cityoptions = new ArrayList<String>();
>>>>>>> master
        this.imageUrl = city.getImageUrl();
    }

    @Autowired

    public void generateNextRound() {
        currentRound = rounds.size() + 1;
        if (currentRound > totalRounds) {
            return;
        }
        Round round = new Round(this, currentRound, city, countdownTime);
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

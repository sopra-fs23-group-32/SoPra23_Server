package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Round {
    private Game game;
    private int roundNumber;
    private City city;
    private CityBase cityDB;
    private String imageUrl;
    private int countdownTime;
    private Map<Player, Integer> playerScores;

    public Round(Game game, int roundNumber, CityBase cityDB, int countdownTime) {
        this.game = game;
        this.roundNumber = roundNumber;
        this.cityDB = cityDB;
        this.countdownTime = countdownTime;
        this.playerScores = new HashMap<>();
    }

    public String getImageUrl() {
        City city = getRandomCity();
        String cityName = city.name;
        return "flickr city image url";
    }

    public String getCorrectAnswer() {
        return city.name;
    }

    private City getRandomCity() {
        Random rand = new Random();
        int randomIndex  = rand.nextInt(cityDB.getCityListLength());
        City city = cityDB.getCity(randomIndex);
        this.city = city;
        return city;
    }

    public void submitAnswer(Answer answer) {
        Player player = game.getPlayerById(answer.getPlayerId());
        String playerAnswer = answer.getAnswer();
        int timeTaken = answer.getTimeTaken();
        int remainingTime = calculateRemainingTime(timeTaken);

        String correctAnswer = getCorrectAnswer();

        if (playerAnswer.equals(correctAnswer)) {
            int score = calculateScore(remainingTime);
            updatePlayerScore(player, score);
            playerScores.put(player, score);
        } else {
            updatePlayerScore(player, 0);
        }
    }

    private int calculateRemainingTime(int timeTaken) {
        int remainingTime = countdownTime - timeTaken;
        return remainingTime > 0 ? remainingTime : 0;
    }

    private int calculateScore(int remainingTime) {
        // Assume a score of 100 for a correct answer and 10 points deducted for second elapsed
        int score = (100 - (remainingTime / 1000) * 10);
        return score > 0 ? score : 0;
    }

    private void updatePlayerScore(Player player, int score) {
        player.addScore(score);
    }
}

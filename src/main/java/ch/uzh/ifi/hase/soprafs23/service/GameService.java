package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionGetDTO;
import jdk.jfr.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Game Service - The "worker", responsible for all functionality related to the game
 * (creates, modifies, deletes, finds). The result will be passed back to the caller.
 */

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private Game game;
    private String CurrentLabel;

    public void createGame(CityCategory category, int rounds, int countdownTime) {
        CityBase cityDB = new CityBase(category);
        this.game = new Game(rounds, countdownTime, cityDB);
    }

    public void addPlayer(User user) {
        game.addPlayer(user);
    }

    public Question goNextRound() {
        game.addCurrentRound();
        if(game.isGameEnded()){
            return new Question("0", "0", "0", "0", "0");
        }
        List<City> citiesDrawn = game.cityDB.drawCities();
        Random random = new Random();
        int intRand = random.nextInt(3);
        // Draw one city to generate picture
        CurrentLabel = citiesDrawn.get(intRand).getName();
        String pictureUrl = "what?";
        // Others just return their name
        return new Question(citiesDrawn.get(0).getName(), citiesDrawn.get(1).getName(),
                citiesDrawn.get(2).getName(), citiesDrawn.get(3).getName(), pictureUrl);
    }

    /**
     * Add the answer to the player's list and update the points
     * @param playerId player's ID
     * @param answer an Answer object
     */
    public int submitAnswer(Long playerId, Answer answer) {
        Player currentPlayer = searchPlayerById(playerId);
        currentPlayer.addAnswer(answer.getAnswer());
        // get the right answer of current round
        int score = 0;
        String correctAnswer = CurrentLabel;
        if (answer.getAnswer().equals(correctAnswer)) {
            int remainingTime = game.getCountdownTime() - answer.getTimeTaken();
            score = calculateScore(Math.max(remainingTime, 0));
            currentPlayer.addScore(score);
        }
        return score;
    }

    private int calculateScore(int remainingTime) {
        // 50 pts for a correct answer and 10 pts for each second remains
        return 50 + (remainingTime * 10);
    }

    public Player searchPlayerById(Long playerId) {
        Iterator<Player> playerIterator = game.getPlayerList();
        while(playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if(Objects.equals(player.getUserId(), playerId)) {
                return player;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Player with ID %d was not found!\n", playerId));
    }
}
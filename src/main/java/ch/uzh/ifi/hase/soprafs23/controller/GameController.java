package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.CityBase;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller - Responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution
 * to the UserService and finally return the result.
 */
@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Start the game
     * @param rounds The number of rounds
     * @param countdownTime The countdown time of each round
     * @param cityDB The city database
     */
    @PostMapping("/game/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startGame(@RequestParam int rounds, @RequestParam int countdownTime, @RequestBody CityBase cityDB) {
        gameService.startNewGame(rounds, countdownTime, cityDB);
    }

    /**
     * Submit the answers of players
     * @param answers The list of answers, including the timeTaken and answer of each player
     */
    @PostMapping("/game/answers")
    @ResponseStatus(HttpStatus.OK)
    public void submitAnswers(@RequestBody List<Answer> answers) {
        gameService.submitAnswers(answers);
    }

}

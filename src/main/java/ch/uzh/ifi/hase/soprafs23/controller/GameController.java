package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller - Responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution
 * to the UserService and finally return the result.
 */
@RestController
public class GameController {
    private final UserService userService;
    private final GameService gameService;

    GameController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    /**
     * Start the game
     * @param category The city category
     * @param totalRounds The number of rounds
     * @param countdownTime The countdown time of each round
     */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createGame(@RequestBody CityCategory category,
                           @RequestParam int totalRounds,
                           @RequestParam int countdownTime) {
        gameService.createGame(category, totalRounds, countdownTime);
    }

    /**
     * Go to the next round of the game
     * @return QuestionDTO Return a DTO including - 4 options of String, the url of the picture
     */
    @PutMapping("/games")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public QuestionGetDTO goNextRound() {
        Question question = gameService.goNextRound();
        return DTOMapper.INSTANCE.convertEntityToQuestionGetDTO(question);
    }

    /**
     * Add players to the game
     * @param playerId userId of the player
     */
    @PostMapping("/games/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public void createPlayer(@PathVariable Long playerId) {
//        Player newPlayer = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);
        User user = userService.searchUserById(playerId);
        gameService.addPlayer(user);
    }

    /**
     * Submit the answers of players
     * @param answerPostDTO the answerDTO submitted by the player
     */
    @PostMapping("/games/{playerId}/answers")
    @ResponseStatus(HttpStatus.OK)
    public void submitAnswer(@RequestBody AnswerPostDTO answerPostDTO, @PathVariable Long playerId) {
        Answer newAnswer = DTOMapper.INSTANCE.convertAnswerPostDTOtoEntity(answerPostDTO);
        gameService.submitAnswer(playerId, newAnswer);
    }

}

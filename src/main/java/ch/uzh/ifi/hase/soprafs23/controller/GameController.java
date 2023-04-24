package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
     * @return DTO for game: gameId, current Round, Score board of player
     */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    public GameGetDTO createGame(@RequestBody GamePostDTO gamePostDTO) {
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);
        Game newGame = gameService.createGame(gameInput);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(newGame);
    }

    /**
     * Go to the next round of the game
     * @param gameId gameId of the game
     * @return QuestionDTO Return a DTO including - 4 options of String, the url of the picture
     */
    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionGetDTO goNextRound(@PathVariable Long gameId) {
        System.out.println("Erreicht");
        Question question = gameService.goNextRound(gameId);
        System.out.println("-----------------");
        System.out.println("Option1: ");
        System.out.println(question.getOption1());

        System.out.println("Option2: ");
        System.out.println(question.getOption2());

        System.out.println("Option3: ");
        System.out.println(question.getOption3());

        System.out.println("Option4: ");
        System.out.println(question.getOption4());

        System.out.println("CorrectOption: ");
        System.out.println(question.getCorrectOption());

        System.out.println("PictureUrl: ");
        System.out.println(question.getPictureUrl());


        System.out.println("-----------------");
        return DTOMapper.INSTANCE.convertEntityToQuestionGetDTO(question);
    }

    /**
     * Get game progress and score board
     * @return DTO for game: gameId, current Round, Score board of player
     */
    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameGetDTO getGame(@PathVariable Long gameId) {
        Game game = gameService.searchGameById(gameId);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    /**
     * Get list of players in a game
     * @return List of user DTO
     */
    @GetMapping("/games/{gameId}/players")
    @ResponseStatus(HttpStatus.OK)
    public List<UserGetDTO> getPlayers(@PathVariable Long gameId) {
        Game game = gameService.searchGameById(gameId);
        List<UserGetDTO> userGetDTOList = new ArrayList<>();
        List<Long> userIdList = gameService.getAllPlayers(game);
        for(long userId: userIdList) {
            userGetDTOList.add(
                DTOMapper.INSTANCE.convertEntityToUserGetDTO(
                    userService.searchUserById(userId)
                )
            );
        }
        return userGetDTOList;
    }

    /**
     * Add players to the game
     * @param gameId gameId of the game
     * @param playerId userId of the player
     */
    @PostMapping("/games/{gameId}/players/{playerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPlayer(@PathVariable Long gameId, @PathVariable Long playerId) {
        User user = userService.searchUserById(playerId);
        gameService.addPlayer(gameId, user);
    }

    /**
     * Submit the answers of players
     * @param gameId gameId of the game
     * @param playerId userId of the player
     * @param answerPostDTO the answerDTO submitted by the player
     * will return the score
     */
    @PostMapping("/games/{gameId}/players/{playerId}/answers")
    @ResponseStatus(HttpStatus.OK)
    public int submitAnswer(@RequestBody AnswerPostDTO answerPostDTO,
                            @PathVariable Long gameId,
                            @PathVariable Long playerId) {
        Answer newAnswer = DTOMapper.INSTANCE.convertAnswerPostDTOtoEntity(answerPostDTO);
        return gameService.submitAnswer(gameId, playerId, newAnswer);
    }

    /**
     * Show the player rankings
     * @param gameId gameId of the game
     * will return the list of PlayerRanking
     */
    @GetMapping("/games/{gameId}/ranking")
    @ResponseStatus(HttpStatus.OK)
    public List<PlayerRankingGetDTO> getRanking(@PathVariable Long gameId) {
        List<PlayerRanking> playerRankingList = gameService.getRanking(gameId);
        List<PlayerRankingGetDTO> playerRankingGetDTOList = new ArrayList<>();
        for (PlayerRanking playerRanking : playerRankingList) {
            playerRankingGetDTOList.add(
                DTOMapper.INSTANCE.convertEntityToPlayerRankingGetDTO(playerRanking)
            );
        }
        return playerRankingGetDTOList;
    }

    /**
     * End the game of MultiPLayer Mode
     * @param gameId gameId of the game
     * will return GameResultGetDTO, including a list of winners and a list of PlayerRanking
     */
    @GetMapping("/games/{gameId}/results")
    @ResponseStatus(HttpStatus.OK)
    public GameResultGetDTO endGame(@PathVariable Long gameId) {
        GameResult gameResult = gameService.getGameResult(gameId);
        return DTOMapper.INSTANCE.convertEntityToGameResultGetDTO(gameResult);
    }
}

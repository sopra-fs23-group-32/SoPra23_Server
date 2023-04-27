package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;

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


     @GetMapping("/gamestatus/{gameId}")
     @ResponseStatus(HttpStatus.OK)
     public GameStatus getGameStatus(@PathVariable Long gameId) {
         Game game = gameService.searchGameById(gameId);
         GameStatus gameStatus=game.getGameStatus();
         System.out.println("GameStauts Start: "+gameStatus+"GameStatus End");
         return gameStatus;
     }

     

    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionGetDTO goNextRound(@PathVariable Long gameId) {
        Question question = gameService.goNextRound(gameId);
        System.out.println("-----------------");
        System.out.println("Option1: " + question.getOption1());
        System.out.println("Option2: " + question.getOption2());
        System.out.println("Option3: " + question.getOption3());
        System.out.println("Option4: " + question.getOption4());

        System.out.println("CorrectOption: " + question.getCorrectOption());
        System.out.println("PictureUrl: " + question.getPictureUrl());
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



    @DeleteMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeGame(@PathVariable Long gameId) {
        gameService.closeGame(gameId);
        System.out.println("Game deleted!");
    }

    /**
     * Get list of players in a game
     * @return List of user DTO
     */
    @GetMapping("/games/{gameId}/players")
    @ResponseStatus(HttpStatus.OK)
    public List<UserGetDTO> getPlayers(@PathVariable Long gameId) {
        List<Long> userIdList = gameService.getAllPlayers(gameId);
        List<UserGetDTO> userGetDTOList = new ArrayList<>();
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
    public UserGetDTO createPlayer(@PathVariable Long gameId, @PathVariable Long playerId) {
        User user = userService.searchUserById(playerId);
        gameService.addPlayer(gameId, user);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @DeleteMapping("/games/{gameId}/players/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeGame(@PathVariable Long gameId, @PathVariable Long playerId) {
        gameService.leaveGame(gameId, playerId);
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
        int score = gameService.submitAnswer(gameId, playerId, newAnswer);
        boolean allAnswered = gameService.checkIfAllAnswered(gameId);
        String tag = "False";
        if(allAnswered) {
            tag = "True";
        }
        System.out.printf("From playerID %d - Submit answer. AllAnswered: %s\n", playerId, tag);
        System.out.println("++++++++Score: "+score+"Score End+++++++11");
        return score;
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

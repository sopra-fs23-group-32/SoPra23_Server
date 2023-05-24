package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.WebSocketType;
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

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getGames() {
        List<Game> allGames = gameService.getAllGames();
        List<GameGetDTO> gameGetDTOList = new ArrayList<>();
        for (Game game: allGames) {
            if (game.getGameStatus() == GameStatus.SETUP) {
                gameGetDTOList.add(
                    DTOMapper.INSTANCE.convertEntityToGameGetDTO(game)
                );
            }
        }
        return gameGetDTOList;
    }

    /**
     * Get game progress
     * @return DTO for game: gameId, current Round, Score board of player
     */
    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameGetDTO getGame(@PathVariable Long gameId) {
        Game game = gameService.searchGameById(gameId);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

     @GetMapping("/games/{gameId}/status")
     @ResponseStatus(HttpStatus.OK)
     public String getGameStatus(@PathVariable Long gameId) {
         Game game = gameService.searchGameById(gameId);
         GameStatus gameStatus=game.getGameStatus();
         return gameStatus.toString();
     }

    /**
     * Go to the next round of the game
     * @param gameId gameId of the game
     * @return QuestionDTO Return a DTO including - 4 options of String, the url of the picture
     */
    @PutMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionGetDTO goNextRound(@PathVariable Long gameId) {
        Game game = gameService.searchGameById(gameId);
        // tell guests that game has started
        if(game.getGameStatus().equals(GameStatus.SETUP)) {
            if(game.getTotalRounds() > 1000) {
                game.setPlayerNumForSur();
            }
            game.setGameStatus(GameStatus.WAITING);
            gameService.updateGameStatus(gameId, WebSocketType.GAME_START, game.getGameStatus());
        }
        Question question = gameService.goNextRound(gameId);
        return DTOMapper.INSTANCE.convertEntityToQuestionGetDTO(question);
    }

    /**
     *  refresh image for current answer and synchronize to the database
     */
    @PutMapping("/games/{gameId}/refresh")
    @ResponseStatus(HttpStatus.OK)
    public String refreshImage(@PathVariable Long gameId) {
        Game game = gameService.searchGameById(gameId);
        String ImgUrl = game.getImgUrl();
        try {
            ImgUrl = GameService.refreshImage(game.getCurrentAnswer());
//            gameService.updateCurrentImage(game, ImgUrl);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ImgUrl;
    }

    /**
     * Get questions in a game
     * @return List of user DTO
     */
    @GetMapping("/games/{gameId}/questions")
    @ResponseStatus(HttpStatus.OK)
    public QuestionGetDTO getQuestions(@PathVariable Long gameId) {
        Question question = gameService.getQuestions(gameId);
        System.out.println("--> Someone is fetching the question.");
        return DTOMapper.INSTANCE.convertEntityToQuestionGetDTO(question);
    }

    @DeleteMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeGame(@PathVariable Long gameId) {
        gameService.closeGame(gameId);
        System.out.printf("------> Game %d deleted!\n", gameId);
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
    public UserGetDTO addPlayer(@PathVariable Long gameId, @PathVariable Long playerId) {
        User user = userService.searchUserById(playerId);
        gameService.addPlayer(gameId, user);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @DeleteMapping("/games/{gameId}/players/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeGame(@PathVariable Long gameId, @PathVariable Long playerId) {
        gameService.leaveGame(gameId, playerId);
        System.out.printf("----> Player(ID %d) leave Game(ID %d).\n", playerId, gameId);
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
        System.out.printf(
            "----> Game %d - Player(ID %d) submit: %s, score: %d\n",
            gameId, playerId, newAnswer.getAnswer(), score
        );
        if(allAnswered) {System.out.printf("----> Game %d - All Answered!\n", gameId);}
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
     * Get the game result of MultiPLayer Mode
     * @param gameId gameId of the game
     * will return a list of winners's names
     */
    @GetMapping("/games/{gameId}/results")
    @ResponseStatus(HttpStatus.OK)
    public List<String> endGame(@PathVariable Long gameId) {
        return gameService.getGameResult(gameId);
    }
}

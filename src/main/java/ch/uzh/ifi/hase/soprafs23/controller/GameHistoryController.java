package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.UserGameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.GameHistoryAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameHistoryGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameInfoGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameHistoryAnswerGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class GameHistoryController {

    private final GameService gameService;
    private final GameHistoryService gameHistoryService;
    private final UserStatisticsService userStatisticsService;

    GameHistoryController(GameService gameService,
                          GameHistoryService gameHistoryService,
                          UserStatisticsService userStatisticsService) {
        this.gameService = gameService;
        this.gameHistoryService = gameHistoryService;
        this.userStatisticsService = userStatisticsService;
    }

    @PostMapping("/gameInfo/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameInfoGetDTO createGameInfo(@PathVariable Long gameId) {
        GameInfo newGameInfo = gameService.getGameInfo(gameId);
        newGameInfo = gameHistoryService.createGameInfo(newGameInfo);
        System.out.printf("GameInfo for Game %d created.\n", gameId);
        return DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(newGameInfo);
    }

    @PostMapping("/users/{userId}/gameHistories/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameHistoryGetDTO createUserGameHistory(@PathVariable Long userId, @PathVariable Long gameId) {
        GameInfo newGameInfo = gameService.getGameInfo(gameId);
        UserGameHistory newGameHistory = gameService.getUserGameHistory(gameId, userId);
        userStatisticsService.addUserGameHistory(userId, newGameHistory);
        userStatisticsService.updateUserStatistics(
            userId, newGameHistory.getGameScore(), newGameInfo.getCategory());
        System.out.printf("GameHistory for Game %d saved by User %d.\n", gameId, userId);
        return DTOMapper.INSTANCE.convertEntityToGameHistoryGetDTO(newGameHistory);
    }

    /**
     * Get all game info. of game histories of a given user (without details)
     * @param userId unique ID for user
     * @return List of GameHistory DTOs w.r.t. userId
     */
    @GetMapping("/users/{userId}/gameInfo")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameInfoGetDTO> getAllGameInfos(@PathVariable Long userId) {
        List<Long> gameIdList = userStatisticsService.getUserGameHistoryIds(userId);
        List<GameInfoGetDTO> gameInfoGetDTOS = new ArrayList<>();
        for(Long gameId : gameIdList){
            GameInfo gameInfo = gameHistoryService.searchGameInfoById(gameId);
            gameInfoGetDTOS.add(DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(gameInfo));
        }
        return gameInfoGetDTOS;
    }

    /**
     * Get all game histories of a given user
     * @param userId unique ID for user
     * @return List of GameHistory DTOs w.r.t. userId
     */
    @GetMapping("/users/{userId}/gameHistories")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameHistoryGetDTO> getAllGameHistories(@PathVariable Long userId) {
        Iterator<UserGameHistory> userGameHistoryIterator = userStatisticsService.getAllUserGameHistory(userId);
        List<GameHistoryGetDTO> gameHistoryGetDTOList = new ArrayList<>();
        while(userGameHistoryIterator.hasNext()){
            UserGameHistory gameHistory = userGameHistoryIterator.next();
            gameHistoryGetDTOList.add(DTOMapper.INSTANCE.convertEntityToGameHistoryGetDTO(gameHistory));
        }
        return gameHistoryGetDTOList;
    }

    /**
     * Get game info. of one game history of a given user
     * @param userId unique ID for user
     * @param gameId unique ID for game history of the user
     * @return GameHistory DTO w.r.t. userId & gameId
     */
    @GetMapping("/users/{userId}/gameInfo/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameInfoGetDTO getOneGameInfo(@PathVariable Long userId, @PathVariable Long gameId) {
        GameInfo gameInfo = gameHistoryService.searchGameInfoById(gameId);
        // and check if this id in userStatistics
        UserGameHistory userGameHistory =
                userStatisticsService.searchUserGameHistoryById(userId, gameId);
        return DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(gameInfo);
    }

    /**
     * Get score of one game history of a given user
     * @param userId unique ID for user
     * @param gameId unique ID for game history of the user
     * @return GameHistory DTO w.r.t. userId & gameId
     */
    @GetMapping("/users/{userId}/gameHistories/{gameId}/stats")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameHistoryGetDTO getGameHistoryScore(@PathVariable Long userId, @PathVariable Long gameId) {
        // check if this gameId exist
        gameHistoryService.checkIfIdExist(gameId);
        UserGameHistory gameHistory = userStatisticsService.searchUserGameHistoryById(userId, gameId);
        return DTOMapper.INSTANCE.convertEntityToGameHistoryGetDTO(gameHistory);
    }

    /**
     * Get list of answers and labels from one game of a given user
     * @param userId unique ID for user
     * @param gameId unique ID for game history of the user
     * @return GameHistoryAnswer DTO w.r.t. userId & gameId
     */
    @GetMapping("/users/{userId}/gameHistories/{gameId}/answer")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public List<GameHistoryAnswerGetDTO> getGameHistoryAnswers(
            @PathVariable Long userId, @PathVariable Long gameId) {
        // check if this gameId exist
        GameInfo gameInfo = gameHistoryService.searchGameInfoById(gameId);
        // and check if this id in userStatistics
        UserGameHistory userGameHistory =
                userStatisticsService.searchUserGameHistoryById(userId, gameId);

        List<GameHistoryAnswerGetDTO> gameHistoryAnswerGetDTOList = new ArrayList<>();
        Iterator<String> answerIterator = userGameHistory.getAnswerList();
        Iterator<String> labelIterator = gameInfo.getLabelList();
        while(answerIterator.hasNext()) {
            gameHistoryAnswerGetDTOList.add(
                    DTOMapper.INSTANCE.convertEntityToGameHistoryAnswerGetDTO(
                            new GameHistoryAnswer(answerIterator.next(),
                                    labelIterator.next())
                    )
            );
        }
        return gameHistoryAnswerGetDTOList;
    }

}

package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.UserGameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.GameHistoryAnswer;
import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameInfoGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameHistoryAnswerGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserStatisticsService;
import org.springframework.http.HttpStatus;
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
        return DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(newGameInfo);
    }

    @PostMapping("/users/{userId}/gameInfo/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createUserGameHistory(@PathVariable Long userId, @PathVariable Long gameId) {
        GameInfo newGameInfo = gameService.getGameInfo(gameId);
        UserGameHistory newGameHistory = gameService.getUserGameHistory(gameId, userId);
        userStatisticsService.addUserGameHistory(userId, newGameHistory);
        userStatisticsService.updateUserStatistics(
                userId, newGameHistory.getGameScore(), newGameInfo.getCategory());
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
     * Get game info. of one game history of a given user
     * @param userId unique ID for user
     * @param gameId unique ID for game history of the user
     * @return GameHistory DTO w.r.t. userId & gameId
     */
    @GetMapping("/users/{userId}/gameInfo/{gameId}/details")
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
    @GetMapping("/users/{userId}/gameInfo/{gameId}/score")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public int getGameHistoryScore(@PathVariable Long userId, @PathVariable Long gameId) {
        // check if this gameId exist
        gameHistoryService.checkIfIdExist(gameId);
        UserGameHistory userGameHistory =
                userStatisticsService.searchUserGameHistoryById(userId, gameId);
        return userGameHistory.getGameScore();
    }

    /**
     * Get list of answers and labels from one game of a given user
     * @param userId unique ID for user
     * @param gameId unique ID for game history of the user
     * @return GameHistoryAnswer DTO w.r.t. userId & gameId
     */
    @GetMapping("/users/{userId}/gameInfo/{gameId}/answer")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
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

package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WebSocketType;
import ch.uzh.ifi.hase.soprafs23.entity.*;
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

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class GameHistoryController {

    private final GameService gameService;
    private final GameHistoryService gameHistoryService;
    private final UserStatisticsService userStatisticsService;
    private final Lock lock = new ReentrantLock();
    private final Condition executionCondition = lock.newCondition();
//    private boolean isExecuted = false;
    private final Map<Long, Boolean> isExecuted = new HashMap<>();

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
        Game game = gameService.searchGameById(gameId);
        GameInfo newGameInfo = gameService.getGameInfo(gameId);

        if (game.getTotalRounds() == 10000) {
            newGameInfo = gameHistoryService.createGameInfo(newGameInfo);
            System.out.printf("GameInfo for Game %d created.\n", gameId);
            if(game.getPlayerNum() == 0) {
                // delete the Survival Mode at the end
                gameService.deleteGame(game);
            }
            return DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(newGameInfo);
        }

        if (!isExecuted.containsKey(gameId)) {isExecuted.put(gameId, false);}

        // only invoked once for normal
        if (!isExecuted.get(gameId)) {
            lock.lock();
            try {
                // double lock
                if (!isExecuted.get(gameId)) {
                    if (!game.getGameStatus().equals(GameStatus.ENDED) && !game.getGameStatus().equals(GameStatus.DELETED)) {
                        isExecuted.put(gameId, true);
                        if (game.getTotalRounds() < 1001 && game.isGameEnded()) {
                            game.setGameStatus(GameStatus.ENDED);
                            gameService.updateGameStatus(gameId, WebSocketType.GAME_END, game.getGameStatus());
                            newGameInfo = gameHistoryService.createGameInfo(newGameInfo);
                            System.out.printf("GameInfo for Game %d created.\n", gameId);
                        }
                        executionCondition.signal();
                        isExecuted.put(gameId, false);
                    }
                }
            } finally {
                lock.unlock();
            }
        } else {
            lock.lock();
            try {
                executionCondition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }


        return DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(newGameInfo);
    }

    @PostMapping("/users/{userId}/gameHistories/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameHistoryGetDTO createUserGameHistory(@PathVariable Long userId, @PathVariable Long gameId) {
        GameInfo newGameInfo = gameService.getGameInfo(gameId);
        UserGameHistory newHistory = gameService.getUserGameHistory(gameId, userId);
        userStatisticsService.addUserGameHistory(userId, newHistory);
        if(newGameInfo.getPlayerNum() > 1){
            userStatisticsService.updateUserStatistics(userId, newHistory.getGameScore(), newGameInfo.getCategory());
        }
        System.out.printf("GameHistory for Game %d saved by User %d.\n", gameId, userId);
        return DTOMapper.INSTANCE.convertEntityToGameHistoryGetDTO(newHistory);
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
        isExecuted.keySet().removeIf(gameIdList::contains);
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
        while(labelIterator.hasNext()) {
            String answer = "GameEnd";
            if(answerIterator.hasNext()){answer = answerIterator.next();}
            gameHistoryAnswerGetDTOList.add(
                DTOMapper.INSTANCE.convertEntityToGameHistoryAnswerGetDTO(
                    new GameHistoryAnswer(answer, labelIterator.next())
                )
            );
        }
        return gameHistoryAnswerGetDTOList;
    }

}

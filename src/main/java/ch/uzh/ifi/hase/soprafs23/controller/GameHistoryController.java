package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserGameHistory;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameHistoryGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameInfoGetDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.GameHistoryService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameHistoryController {

    private final UserService userService;
    private final GameHistoryService gameHistoryService;

    GameHistoryController(UserService userService, GameHistoryService gameHistoryService) {
        this.userService = userService;
        this.gameHistoryService = gameHistoryService;
    }

    /**
     * Get all game info. of game histories of a given user (without details)
     * @param userId unique ID for user
     * @return List of GameHistory DTOs w.r.t. userId
     */
    @GetMapping("/users/{userId}/gameInfos")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameInfoGetDTO> getAllGameHistories(@PathVariable Long userId) {
        User user = userService.searchUserById(userId);
        List<GameInfo> gameInfos = gameHistoryService.getUserGameInfos(user.userStatistics);
        List<GameInfoGetDTO> gameInfoGetDTOS = new ArrayList<>();
        for(GameInfo gameInfo : gameInfos){
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
    @GetMapping("/users/{userId}/gameInfos/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameInfoGetDTO getOneGameHistory(@PathVariable Long userId, @PathVariable Long gameId) {
        User user = userService.searchUserById(userId);
        GameInfo gameInfo = gameHistoryService.searchGameInfoById(user.userStatistics, gameId);
        return DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(gameInfo);
    }

    /**
     * Get details of one game history of a given user
     * @param userId unique ID for user
     * @param gameId unique ID for game history of the user
     * @return GameHistory DTO w.r.t. userId & gameId
     */
    @GetMapping("/users/{userId}/gameInfos/{gameId}/details")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameHistoryGetDTO getOneGameHistoryDetails(@PathVariable Long userId, @PathVariable Long gameId) {
        User user = userService.searchUserById(userId);
        UserGameHistory gameHistory =
                gameHistoryService.searchGameHistoryById(user.userStatistics, gameId);
        return DTOMapper.INSTANCE.convertEntityToGameHistoryGetDTO(gameHistory);
    }

}

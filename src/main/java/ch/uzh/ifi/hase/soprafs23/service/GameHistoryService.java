package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.entity.UserGameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.UserStatistics;
import ch.uzh.ifi.hase.soprafs23.repository.GameInfoRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GameHistory Service - The "worker", responsible for all functionality related to the gameHistory and gameInfo
 * (creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameHistoryService {

    private final GameInfoRepository gameInfoRepository;

    public GameHistoryService(@Qualifier("gameInfoRepository") GameInfoRepository gameInfoRepository) {
        this.gameInfoRepository = gameInfoRepository;
    }

    public List<GameInfo> getUserGameInfos(UserStatistics userStatistics) {
        List<GameInfo> gameInfoList = new ArrayList<>();
        Iterator<UserGameHistory> userGameHistoryIterator = userStatistics.getGameHistoryList();
        while(userGameHistoryIterator.hasNext()) {
            Long gameId = userGameHistoryIterator.next().getGameId();
            gameInfoList.add(gameInfoRepository.findByGameId(gameId));
        }
        return gameInfoList;
    }

    public GameInfo searchGameInfoById(UserStatistics userStatistics, Long gameId) {
        checkIfIdExist(gameId);
        return gameInfoRepository.findByGameId(gameId);
    }

    public UserGameHistory searchGameHistoryById(UserStatistics userStatistics, Long gameId) {
        checkIfIdExist(gameId);
        return checkIfIdInStatistics(userStatistics, gameId);
    }

    private UserGameHistory checkIfIdInStatistics(UserStatistics userStatistics, Long gameId) {
        UserGameHistory gameHistoryById = null;
        Iterator<UserGameHistory> userGameHistoryIterator = userStatistics.getGameHistoryList();
        while(userGameHistoryIterator.hasNext()) {
            UserGameHistory nextGameHistory = userGameHistoryIterator.next();
            if(nextGameHistory.getGameId() == gameId) {
                gameHistoryById = nextGameHistory;
            }
        }
        if(gameHistoryById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("GameHistory with ID %d was not found!\n", gameId));
        }
        return gameHistoryById;
    }

    private void checkIfIdExist(Long gameId) {
        GameInfo gameInfoById = gameInfoRepository.findByGameId(gameId);
        if(gameInfoById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("GameInfo with ID %d was not found!\n", gameId));
        }
    }
}

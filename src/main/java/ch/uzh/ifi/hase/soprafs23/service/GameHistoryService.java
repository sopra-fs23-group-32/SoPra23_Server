package ch.uzh.ifi.hase.soprafs23.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.repository.GameInfoRepository;

import java.util.Date;

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

    public GameInfo createGameInfo(GameInfo newGameInfo) {
        newGameInfo.setGameDate(new Date());
        newGameInfo = gameInfoRepository.save(newGameInfo);
        gameInfoRepository.flush();
        return newGameInfo;
    }

    public GameInfo searchGameInfoById(Long gameId) {
        checkIfIdExist(gameId);
        return gameInfoRepository.findByGameId(gameId);
    }

    // ======================== Supporting functions ===========================
    public void checkIfIdExist(Long gameId) {
        GameInfo gameInfoById = gameInfoRepository.findByGameId(gameId);
        if(gameInfoById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("GameInfo with ID %d was not found!\n", gameId));
        }
    }
}

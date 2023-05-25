package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameInfoRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
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

    public void saveRepoToDatabase() {
        List<GameInfo> gameInfoList = gameInfoRepository.findAll();
        String filePath = "../database/gameInfoRepository.csv";
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.append("gameId,category,gameDate,gameRounds,playerNum,labelList");
            writer.append(System.lineSeparator());
            // write all records
            for (GameInfo gameInfo : gameInfoList) {
                writer.append(gameInfo.getGameId().toString()).append(",");
                writer.append(gameInfo.getCategory().toString()).append(",");
                writer.append(gameInfo.getGameDate().toString()).append(",");
                writer.append(String.valueOf(gameInfo.getGameRounds())).append(",");
                writer.append(String.valueOf(gameInfo.getPlayerNum())).append(",");
                writer.append("[");
                Iterator<String> labelList = gameInfo.getLabelList();
                while(labelList.hasNext()){
                    writer.append(labelList.next()).append(",");
                }
                writer.append("]");
                writer.append(System.lineSeparator());
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

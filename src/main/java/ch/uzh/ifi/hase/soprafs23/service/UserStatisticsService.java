package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.entity.UserGameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.UserStatistics;
import ch.uzh.ifi.hase.soprafs23.repository.UserStatisticsRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class UserStatisticsService {

    private final UserStatisticsRepository userStatisticsRepository;

    public UserStatisticsService(
        @Qualifier("userStatisticsRepository")UserStatisticsRepository userStatisticsRepository) {
        this.userStatisticsRepository = userStatisticsRepository;
    }

    public UserStatistics createUserService(Long userId) {
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setUserId(userId);
        userStatistics.initUserStatistics();
        userStatistics = userStatisticsRepository.save(userStatistics);
        userStatisticsRepository.flush();
        return userStatistics;
    }

    public void addUserGameHistory(Long userId, UserGameHistory userGameHistory) {
        UserStatistics userStatistics = searchUserStatisticsById(userId);
        userGameHistory.setUserId(userId);
        userGameHistory.setUserStatistics(userStatistics);
        userStatistics.addGameHistory(userGameHistory);
    }

    public void updateUserStatistics(Long userId, int score, CityCategory category) {
        UserStatistics userStatistics = searchUserStatisticsById(userId);
        userStatistics.updateScore(score, category);
        userStatistics.updateGameNum(category);
    }

    public long getUserTotalScore(Long userId) {
        return searchUserStatisticsById(userId).getTotalScore();
    }

    public long getUserSpecificScore(Long userId, CityCategory category) {
        return searchUserStatisticsById(userId).getSpecificScore(category);
    }

    public long getUserTotalGameNum(Long userId) {
        return searchUserStatisticsById(userId).getTotalGameNum();
    }

    public long getUserSpecificGameNum(Long userId, CityCategory category) {
        return searchUserStatisticsById(userId).getSpecificGameNum(category);
    }

    // ======================== Supporting functions ===========================

    public Iterator<UserGameHistory> getAllUserGameHistory(Long userId) {
        UserStatistics userStatistics = searchUserStatisticsById(userId);
        return userStatistics.getGameHistoryList();
    }

    public List<Long> getUserGameHistoryIds(Long userId) {
        UserStatistics userStatistics = searchUserStatisticsById(userId);
        List<Long> gameIdList = new ArrayList<>();
        Iterator<UserGameHistory> userGameHistoryIterator = userStatistics.getGameHistoryList();
        while(userGameHistoryIterator.hasNext()) {
            gameIdList.add(userGameHistoryIterator.next().getGameId());
        }
        return gameIdList;
    }

    public UserGameHistory searchUserGameHistoryById(Long userId, Long gameId) {
        UserStatistics userStatistics = searchUserStatisticsById(userId);
        UserGameHistory gameHistoryById = null;
        Iterator<UserGameHistory> userGameHistoryIterator = userStatistics.getGameHistoryList();
        while(userGameHistoryIterator.hasNext()) {
            UserGameHistory gameHistory = userGameHistoryIterator.next();
            if(gameHistory.getGameId() == gameId) {
                gameHistoryById = gameHistory;
            }
        }
        if(gameHistoryById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("GameHistory with ID %d was not found!\n", gameId));
        }
        return gameHistoryById;
    }

    public UserStatistics searchUserStatisticsById(Long userId) {
        checkIfIdExist(userId);
        return userStatisticsRepository.findByUserId(userId);
    }

    void checkIfIdExist(Long userId) {
        UserStatistics userStatisticsById = userStatisticsRepository.findByUserId(userId);
        if(userStatisticsById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("UserStatistics with ID %d was not found!\n", userId));
        }
    }
}

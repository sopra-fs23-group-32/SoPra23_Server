package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WebSocketType;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserRankingGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional
public class ScoreBoardService {

    private final UserService userService;
    private final UserStatisticsService userStatisticsService;
    private final Logger log = LoggerFactory.getLogger(ScoreBoardService.class);

    public ScoreBoardService(UserService userService, UserStatisticsService userStatisticsService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.userStatisticsService = userStatisticsService;
    }

    public List<UserRanking> getUserRanking(CityCategory category) {
        List<User> userList = userService.getUsers();
        List<Long> userIdList = new ArrayList<>();
        for(User user:userList) {
            userIdList.add(user.getUserId());
        }

        List<Long> sortedUserIdList = userIdList.stream().sorted(
                Comparator.comparing(userStatisticsService::getUserTotalScore).reversed()).toList();
        if(category != null) {
            sortedUserIdList = userIdList.stream().sorted(
                    Comparator.comparing(
                            userId -> userStatisticsService.getUserSpecificScore((Long) userId, category)).reversed()
            ).toList();
        }

        List<UserRanking> userRankingList = new ArrayList<>();
        int currentRank = 0;
        long currentScore = Long.MAX_VALUE;
        for(Long userId:sortedUserIdList) {
            long userScore = userStatisticsService.getUserTotalScore(userId);
            long userGameNum = userStatisticsService.getUserTotalGameNum(userId);
            if(category != null) {
                userScore = userStatisticsService.getUserSpecificScore(userId, category);
                userGameNum = userStatisticsService.getUserSpecificGameNum(userId, category);
            }
            if (userScore < currentScore) {
                currentRank ++;
                currentScore = userScore;
            }

            User user = userService.searchUserById(userId);
            UserRanking userRanking = new UserRanking(
                    user.getUserId(), user.getUsername(), user.getCreateDay(),
                    userScore, userGameNum, currentRank
            );
            userRankingList.add(userRanking);
        }
        return userRankingList;
    }
}

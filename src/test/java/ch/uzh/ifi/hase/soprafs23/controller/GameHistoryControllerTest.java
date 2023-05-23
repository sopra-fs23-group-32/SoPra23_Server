package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import ch.uzh.ifi.hase.soprafs23.entity.*;

import ch.uzh.ifi.hase.soprafs23.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.UserStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.format.DateTimeFormatter;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GameHistoryControllerTest
 * This is a WebMvcTest which allows to test the GameHistoryController i.e. GET
 * request without actually sending them over the network.
 * This tests if the GameHistoryController works.
 */
@WebMvcTest(GameHistoryController.class)
public class GameHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameHistoryService gameHistoryService;
    @MockBean
    private UserService userService;
    @MockBean
    private GameService gameService;
    @MockBean
    private UserStatisticsService userStatisticsService;

    private User user;
    private UserGameHistory gameHistory;
    private GameInfo gameInfo;

    @BeforeEach
    public void setUp() {
        Long userId = 1L;
        Long gameId = 1L;

        gameHistory = new UserGameHistory();
        gameHistory.setGameId(gameId);
        gameHistory.setGameScore(200);
        gameHistory.addAnswer("Zurich");
        gameHistory.addAnswer("Basel");
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.addGameHistory(gameHistory);
        user = new User();
        user.setUserId(userId);
        given(userService.searchUserById(eq(userId))).willReturn(user);

        gameInfo = new GameInfo();
        gameInfo.setGameId(gameId);
        gameInfo.setCategory(CityCategory.EUROPE);
        gameInfo.setGameDate(new Date());
        gameInfo.setGameRounds(2);
        gameInfo.setPlayerNum(2);
        gameInfo.addLabel("Zurich");
        gameInfo.addLabel("Bern");
        given(gameHistoryService.searchGameInfoById(eq(gameId))).willReturn(gameInfo);
    }

    @Test
    public void testCreateGameInfo() throws Exception {
        Long gameId = 1L;

        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameId(gameId);
        gameInfo.setCategory(CityCategory.EUROPE);
        gameInfo.setGameDate(new Date());
        gameInfo.setGameRounds(5);
        gameInfo.setPlayerNum(4);

        GameInfo newGameInfo = gameInfo;

        given(gameService.getGameInfo(eq(gameId))).willReturn(gameInfo);
        given(gameHistoryService.createGameInfo(any(GameInfo.class))).willReturn(newGameInfo);

        MockHttpServletRequestBuilder postRequest = post("/gameInfo/{gameId}", gameId)
                .contentType(MediaType.APPLICATION_JSON);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        // Perform the POST request
        mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.gameId", is(gameInfo.getGameId().intValue())))
            .andExpect(jsonPath("$.category", is(gameInfo.getCategory().toString())))
            .andExpect(jsonPath("$.gameDate", notNullValue()))
            .andExpect(jsonPath("$.gameRounds", is(gameInfo.getGameRounds())))
            .andExpect(jsonPath("$.playerNum", is(gameInfo.getPlayerNum())));
    }

    @Test
    public void testCreateUserGameHistory() throws Exception {
        // given
        Long gameId = 1L;
        Long userId = 1L;
        given(gameService.getGameInfo(eq(gameId))).willReturn(gameInfo);
        given(gameService.getUserGameHistory(eq(gameId), eq(userId))).willReturn(gameHistory);

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/gameHistories/{gameId}", gameId, userId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the POST request
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId", is(gameHistory.getGameId().intValue())))
                .andExpect(jsonPath("$.gameScore", is(gameHistory.getGameScore())));

    }

    @Test
    public void testGetAllGameInfos() throws Exception {
        // given
        Long userId = 1L;
        List<Long> gameIdList = Collections.singletonList(gameInfo.getGameId());
        given(userStatisticsService.getUserGameHistoryIds(eq(user.getUserId()))).willReturn(gameIdList);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/gameInfo", userId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].gameId", is(gameInfo.getGameId().intValue())))
            .andExpect(jsonPath("$[0].category", is(gameInfo.getCategory().toString())))
            .andExpect(jsonPath("$[0].gameRounds", is(gameInfo.getGameRounds())))
            .andExpect(jsonPath("$[0].playerNum", is(gameInfo.getPlayerNum())));
    }

    @Test
    public void testGetAllGameHistories() throws Exception {
        // given
        Long userId = 1L;
        List<UserGameHistory> userGameHistoryList = Collections.singletonList(gameHistory);
        Iterator<UserGameHistory> userGameHistoryIterator = userGameHistoryList.iterator();
        given(userStatisticsService.getAllUserGameHistory(eq(user.getUserId()))).willReturn(userGameHistoryIterator);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/gameHistories", userId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameId", is(gameHistory.getGameId().intValue())))
                .andExpect(jsonPath("$[0].gameScore", is(gameHistory.getGameScore())));
    }

    @Test
    public void testGetOneGameInfo() throws Exception {
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/gameInfo/{gameId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(gameInfo.getGameId().intValue())))
                .andExpect(jsonPath("$.category", is(gameInfo.getCategory().toString())))
                .andExpect(jsonPath("$.gameRounds", is(gameInfo.getGameRounds())))
                .andExpect(jsonPath("$.playerNum", is(gameInfo.getPlayerNum())));
    }

    @Test
    public void testGetGameHistoryScore() throws Exception {
        //given
        given( userStatisticsService.searchUserGameHistoryById(1L, 1L)).willReturn(gameHistory);

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/gameHistories/{gameId}/stats", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameScore").value(equalTo(gameHistory.getGameScore()), int.class))
                .andExpect(jsonPath("$.correctRate").value(equalTo(gameHistory.getCorrectRate()), float.class));
    }

    @Test
    public void testGetGameHistoryAnswers() throws Exception {
        // given
        given(userStatisticsService.searchUserGameHistoryById(1L, 1L)).willReturn(gameHistory);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/gameHistories/{gameId}/answer", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON);
        Iterator<String> answerIterator = gameHistory.getAnswerList();
        Iterator<String> correctAnswerIterator = gameInfo.getLabelList();
        List<String> answerList = new ArrayList<>();
        List<String> correctAnswerList = new ArrayList<>();
        while (answerIterator.hasNext() && correctAnswerIterator.hasNext()) {
            answerList.add(answerIterator.next());
            correctAnswerList.add(correctAnswerIterator.next());
        }
        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].answer", is(answerList.get(0))))
                .andExpect(jsonPath("$[0].correctAnswer", is(correctAnswerList.get(0))))
                .andExpect(jsonPath("$[1].answer", is(answerList.get(1))))
                .andExpect(jsonPath("$[1].correctAnswer", is(correctAnswerList.get(1))));
    }
}
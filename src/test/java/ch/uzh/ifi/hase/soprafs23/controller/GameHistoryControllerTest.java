package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import ch.uzh.ifi.hase.soprafs23.entity.*;

import ch.uzh.ifi.hase.soprafs23.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
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
    private User user;
    private UserGameHistory gameHistory;

    @BeforeEach
    public void setUp() {
        Long userId = 1L;
        gameHistory = new UserGameHistory(1L, 200);
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.addGameHistory(gameHistory);
        user = new User();
        user.setUserId(userId);
        user.setUserStatistics(userStatistics);
        given(userService.searchUserById(Mockito.any())).willReturn(user);
    }
    @Test
    public void givenGameInfos_whenGetAllGameHistories_thenReturnJsonArray() throws Exception {
        // given
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameId(1L);
        gameInfo.setCategory(CityCategory.ASIA);
        gameInfo.setGameDate(new Date());
        gameInfo.setGameRounds(2);
        gameInfo.setPlayerNum(4);

        List<GameInfo> allGameInfos = Collections.singletonList(gameInfo);
        // this mocks the GameHistoryService -> we define above what the gameHistoryService should
        // return when getUserGameInfos() is called
        given(gameHistoryService.getUserGameInfos(Mockito.any())).willReturn(allGameInfos);
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/gameInfos", user.getUserId())
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].gameId", is(gameInfo.getGameId().intValue())))
            .andExpect(jsonPath("$[0].category", is(gameInfo.getCategory().toString())))
            .andExpect(jsonPath("$[0].gameRounds", is(gameInfo.getGameRounds())))
            .andExpect(jsonPath("$[0].playerNum", is(gameInfo.getPlayerNum())));
    }

    @Test
    public void givenGameInfo_whenGetGameHistoryDetails_thenReturnJson() throws Exception {
        // given
        GameInfo gameInfo = new GameInfo();
        Long gameId = 1L;
        gameInfo.setGameId(gameId);
        gameInfo.setCategory(CityCategory.ASIA);
        gameInfo.setGameDate(new Date());
        gameInfo.setGameRounds(2);
        gameInfo.setPlayerNum(4);

        given(gameHistoryService.searchGameInfoById(Mockito.any())).willReturn(gameInfo);
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get(
                "/users/{userId}/gameInfos/{gameId}/details", user.getUserId(), gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(gameInfo.getGameId().intValue())))
                .andExpect(jsonPath("$.category", is(gameInfo.getCategory().toString())))
                .andExpect(jsonPath("$.gameRounds", is(gameInfo.getGameRounds())))
                .andExpect(jsonPath("$.playerNum", is(gameInfo.getPlayerNum())));
    }

    @Test
    public void givenGameHistory_whenGetGameHistoryScore_thenReturnJson() throws Exception {
        // given
        Long gameId = 1L;

        given(gameHistoryService.searchGameHistoryById(Mockito.any(), Mockito.any())).willReturn(gameHistory);
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get(
                "/users/{userId}/gameInfos/{gameId}/score", user.getUserId(), gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }
}
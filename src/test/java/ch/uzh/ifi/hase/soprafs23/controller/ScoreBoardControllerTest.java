package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ScoreBoardControllerTest
 * This is a WebMvcTest which allows to test the ScoreBoardController i.e. GET
 * request without actually sending them over the network.
 * This tests if the ScoreBoardController works.
 */
@WebMvcTest(ScoreBoardController.class)
public class ScoreBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ScoreBoardService scoreBoardService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserStatisticsService userStatisticsService;


    @Test
    public void testGetUserRanking() throws Exception {
        // given
        UserRanking userRanking1 = new UserRanking(1L, "user1", new Date(), 100, 3, 1);
        UserRanking userRanking2 = new UserRanking(2L, "user2", new Date(), 40, 2, 2);
        List<UserRanking> userRankingList = new ArrayList<>();
        userRankingList.add(userRanking1);
        userRankingList.add(userRanking2);

        when(scoreBoardService.getUserRanking(Mockito.any())).thenReturn(userRankingList);

        MockHttpServletRequestBuilder getRequest = get("/users/ranking")
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(userRanking1.getUserId().intValue())))
                .andExpect(jsonPath("$[0].username", is(userRanking1.getUsername())))
                .andExpect(jsonPath("$[0].createDay", notNullValue()))
                .andExpect(jsonPath("$[0].score", is(Math.toIntExact(userRanking1.getScore()))))
                .andExpect(jsonPath("$[0].gameNum", is(Math.toIntExact(userRanking1.getGameNum()))))
                .andExpect(jsonPath("$[0].rank", is(Math.toIntExact(userRanking1.getRank()))));
    }

}
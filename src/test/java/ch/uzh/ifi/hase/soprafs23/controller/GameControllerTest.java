package ch.uzh.ifi.hase.soprafs23.controller;


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AnswerPostDTO;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET
 * request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)

public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;
    @MockBean
    private UserService userService;

    @Test
    public void testCreateGame() throws Exception {
        // given
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setCategory(CityCategory.EUROPE);
        gamePostDTO.setTotalRounds(5);
        gamePostDTO.setCountdownTime(10);

        // Convert the GamePostDTO to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String gameJson = objectMapper.writeValueAsString(gamePostDTO);

        Game game= DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);
        when(gameService.createGame(any(Game.class))).thenReturn(game);
        MockHttpServletRequestBuilder postRequest = post("/games")
            .contentType(MediaType.APPLICATION_JSON)
            .content(gameJson);

        // Perform the POST request
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
    }
/*
    @Test
    public void testGetGameStatus() throws Exception {
        // given
        Long gameId = 1L;
        Game game = new Game();
        game.initGame();
        game.setGameId(gameId);
        game.setGameStatus(GameStatus.ANSWERING);

        when(gameService.searchGameById(eq(gameId))).thenReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/status", gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(game.getGameStatus().toString())));
    }
    */
//    @Test
//    public void testGoNextRound() throws Exception {
//        // given
//        Long gameId = 1L;
//        Question question = new Question("Zurich", "Geneva", "Basel", "Bern","Basel", "pictureUrl");
//
//        when(gameService.goNextRound(eq(gameId))).thenReturn(question);
//
//        MockHttpServletRequestBuilder putRequest = put("/games/{gameId}", gameId)
//                .contentType(MediaType.APPLICATION_JSON);
//
//        // Perform the PUT request
//        mockMvc.perform(putRequest)
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.option1", is(question.getOption1())))
//                .andExpect(jsonPath("$.option2", is(question.getOption2())))
//                .andExpect(jsonPath("$.option3", is(question.getOption3())))
//                .andExpect(jsonPath("$.option4", is(question.getOption4())))
//                .andExpect(jsonPath("$.correctOption", is(question.getCorrectOption())))
//                .andExpect(jsonPath("$.pictureUrl", is(question.getPictureUrl())));
//    }

    @Test
    public void testGetQuestions() throws Exception {
        // given
        Long gameId = 1L;
        Question question = new Question("Zurich", "Geneva", "Basel", "Bern","Basel", "pictureUrl");

        when(gameService.getQuestions(eq(gameId))).thenReturn(question);

        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/questions", gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.option1", is(question.getOption1())))
                .andExpect(jsonPath("$.option2", is(question.getOption2())))
                .andExpect(jsonPath("$.option3", is(question.getOption3())))
                .andExpect(jsonPath("$.option4", is(question.getOption4())))
                .andExpect(jsonPath("$.correctOption", is(question.getCorrectOption())))
                .andExpect(jsonPath("$.pictureUrl", is(question.getPictureUrl())));
    }

    @Test
    public void testGetGame() throws Exception {
        // given
        Long gameId = 1L;
        Game game = new Game();
        game.initGame();
        game.setGameId(gameId);
        game.setTotalRounds(5);
        game.setCountdownTime(10);
        game.updateCurrentAnswer("Zurich");

        when(gameService.searchGameById(eq(gameId))).thenReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}", gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(game.getGameId().intValue())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.totalRounds", is(game.getTotalRounds())))
                .andExpect(jsonPath("$.countdownTime", is(game.getCountdownTime())))
                .andExpect(jsonPath("$.currentAnswer", is(game.getCurrentAnswer())));
    }


    @Test
    public void testCloseGame() throws Exception {
        // given
        Long gameId = 1L;

        MockHttpServletRequestBuilder deleteRequest = delete("/games/{gameId}", gameId);

        // Perform the DELETE request
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetPlayers() throws Exception {
        // given
        Long gameId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setUsername("userName");

        List<Long> userIdList = Collections.singletonList(userId);
        when(gameService.getAllPlayers(eq(gameId))).thenReturn(userIdList);
        when(userService.searchUserById(eq(userId))).thenReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/players", gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(user.getUserId().intValue())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())));
    }

    @Test
    public void testCreatePlayer() throws Exception {
        // given
        Long gameId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setUsername("userName");

        when(userService.searchUserById(eq(userId))).thenReturn(user);

        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/players/{playerId}", gameId, userId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the POST request
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(user.getUserId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void testCloseGameForPlayer() throws Exception {
        // given
        Long gameId = 1L;
        Long userId = 1L;

        MockHttpServletRequestBuilder deleteRequest = delete("/games/{gameId}/players/{playerId}", gameId, userId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the DELETE request
        mockMvc.perform(deleteRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSubmitAnswer() throws Exception {
        // given
        Long gameId = 1L;
        Long userId = 1L;

        AnswerPostDTO answerPostDTO = new AnswerPostDTO();
        answerPostDTO.setAnswer("Zurich");
        answerPostDTO.setTimeTaken(2);

        // Convert the answerPostDTO to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String answerJson = objectMapper.writeValueAsString(answerPostDTO);

        int score = 30;
        when(gameService.submitAnswer(eq(gameId), eq(userId), any(Answer.class))).thenReturn(score);
        MockHttpServletRequestBuilder postRequest = post("/games/{gameId}/players/{playerId}/answers", gameId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(answerJson);

        // Perform the POST request
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(equalTo(score), int.class));
    }

    @Test
    public void testGetRanking() throws Exception {
        // given
        Long gameId = 1L;
        PlayerRanking playerRanking = new PlayerRanking("playerName", 100, 1);

        List<PlayerRanking> playerRankingList = Collections.singletonList(playerRanking);

        when(gameService.getRanking(eq(gameId))).thenReturn(playerRankingList);
        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/ranking", gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerName", is(playerRanking.getPlayerName())))
                .andExpect(jsonPath("$[0].score", is(playerRanking.getScore())))
                .andExpect(jsonPath("$[0].rank", is(playerRanking.getRank())));
    }

    @Test
    public void testEndGame() throws Exception {
        // given
        Long gameId = 1L;
        List<String> winnerList = new ArrayList<>(Arrays.asList("userName1", "userName2"));

        when(gameService.getGameResult(eq(gameId))).thenReturn(winnerList);
        MockHttpServletRequestBuilder getRequest = get("/games/{gameId}/results", gameId)
                .contentType(MediaType.APPLICATION_JSON);

        // Perform the GET request
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is(winnerList.get(0))))
                .andExpect(jsonPath("$[1]", is(winnerList.get(1))));
    }
}

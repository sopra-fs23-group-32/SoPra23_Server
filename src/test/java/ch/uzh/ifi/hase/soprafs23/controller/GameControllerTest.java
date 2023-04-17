package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.SingleModeGame;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void testGetRandomCities() throws Exception {
        List<String> cityNames = Arrays.asList("Paris", "New York", "Tokyo", "London", "Sydney");
        when(gameService.getRandomCityNames(anyString(), anyInt())).thenReturn(cityNames);

        mockMvc.perform(get("/random-cities")
                .param("category", "anyCategory")
                .param("populationThreshold", "1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0]", is("Paris")));
    }

    @Test
    public void testSaveCityImage() throws Exception {
        String cityName = "Paris";
        String imageUrl = "http://example.com/image.jpg";
        Map<String, String> request = new HashMap<>();
        request.put("cityName", cityName);

        when(gameService.saveCityImage(cityName)).thenReturn(imageUrl);

        mockMvc.perform(post("/city-image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testStartSingleModeGame() throws Exception {
        Player player = new Player(5, "Max");
        int rounds = 3;
        int countdownTime = 10;
        String category = "anyCategory";
        int populationThreshold = 1000000;
        SingleModeGame game = new SingleModeGame();

        when(gameService.startNewSingleModeGame(5, rounds, countdownTime, category, populationThreshold)).thenReturn(game);

        mockMvc.perform(post("/singlemode/start")
                .param("player", new ObjectMapper().writeValueAsString(player))
                .param("rounds", String.valueOf(rounds))
                .param("countdownTime", String.valueOf(countdownTime))
                .param("category", category)
                .param("populationThreshold", String.valueOf(populationThreshold)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSingleModeGame() throws Exception {
        int gameId = 1;
        SingleModeGame game = new SingleModeGame();

        when(gameService.getSingleModeGame(gameId)).thenReturn(game);

        mockMvc.perform(get("/singlemode/{gameId}", gameId))
                .andExpect(status().isOk());
    }

}
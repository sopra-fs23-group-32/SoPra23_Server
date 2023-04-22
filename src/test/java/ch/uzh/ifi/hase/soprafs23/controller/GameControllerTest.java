package ch.uzh.ifi.hase.soprafs23.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;

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
}

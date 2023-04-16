package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.City;
import ch.uzh.ifi.hase.soprafs23.service.RandomCitiesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RandomCitiesController.class)
public class RandomCitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RandomCitiesService randomCitiesService;

    @Test
    public void testGetRandomCity() throws Exception {
        // setup
        String cityName = "Zurich";
        City city = new City(cityName,null,null);
        when(randomCitiesService.getRandomCities(anyString(), anyInt())).thenReturn(city);

        // execute and assert
        mockMvc.perform(MockMvcRequestBuilders.get("/random-cities")
                        .param("category", "city")
                        .param("populationThreshold", "100000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(cityName));
    }
}

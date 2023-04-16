package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.City;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class RandomCitiesServiceTest {

    private RandomCitiesService randomCitiesService;

    @BeforeEach
    public void setUp() {
        randomCitiesService = new RandomCitiesService();
    }

    @Test
    public void testGetRandomCities() {
        City city = randomCitiesService.getRandomCities("Europe", 2000000);
        Assertions.assertNotNull(city);
        Assertions.assertEquals(5, city.getCityoptions().size());
    }
}

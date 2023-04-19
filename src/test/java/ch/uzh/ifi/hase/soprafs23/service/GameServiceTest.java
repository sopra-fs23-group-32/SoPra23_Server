package ch.uzh.ifi.hase.soprafs23.service;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class GameServiceTest {
    @Mock
    @InjectMocks
    private GameService gameService;
    @Test


    @Test
    public void testSaveCityImageWithInvalidCityName() {
        // Arrange
        String cityName = "NotACity";

        // Act
        Optional<String> result = gameService.saveCityImage(cityName);

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

}

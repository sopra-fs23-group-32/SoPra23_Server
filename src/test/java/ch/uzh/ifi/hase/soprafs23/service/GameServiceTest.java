package ch.uzh.ifi.hase.soprafs23.service;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import ch.uzh.ifi.hase.soprafs23.service.GameService;

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

    public void testSaveCityImageWithValidCityName() {
        // Arrange
        String cityName = "Zurich";

        // Act
        Optional<String> result = gameService.saveCityImage(cityName);

        // Assert
        Assertions.assertTrue(result.isPresent());
        String filename = result.get();
        File file = new File("C:\\Users\\a\\Desktop\\sopra\\no_git\\server\\src\\main\\java\\ch\\uzh\\ifi\\hase\\soprafs23\\static" + filename);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.delete());
    }


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

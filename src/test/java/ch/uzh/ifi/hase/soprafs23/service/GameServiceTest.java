package ch.uzh.ifi.hase.soprafs23.service;
import org.junit.jupiter.api.Assertions;

import java.io.File;

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
        String result = gameService.saveCityImage(cityName);

        // Assert
        String filename = result;
        File file = new File("C:\\Users\\a\\Desktop\\sopra\\no_git\\server\\src\\main\\java\\ch\\uzh\\ifi\\hase\\soprafs23\\static" + filename);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.delete());
    }

}

package ch.uzh.ifi.hase.soprafs23.service;
import org.junit.jupiter.api.Assertions;

import java.io.File;
<<<<<<< HEAD
import java.util.Optional;
=======
>>>>>>> master

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class GameServiceTest {
    @Mock
    @InjectMocks
    private GameService gameService;
<<<<<<< HEAD
=======

    
>>>>>>> master
    @Test

    public void testSaveCityImageWithValidCityName() {
        // Arrange
        String cityName = "Zurich";

        // Act
<<<<<<< HEAD
        Optional<String> result = gameService.saveCityImage(cityName);

        // Assert
        Assertions.assertTrue(result.isPresent());
        String filename = result.get();
=======
        String result = gameService.saveCityImage(cityName);

        // Assert
        String filename = result;
>>>>>>> master
        File file = new File("C:\\Users\\a\\Desktop\\sopra\\no_git\\server\\src\\main\\java\\ch\\uzh\\ifi\\hase\\soprafs23\\static" + filename);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertTrue(file.delete());
    }

<<<<<<< HEAD

    @Test
    public void testSaveCityImageWithInvalidCityName() {
        // Arrange
        String cityName = "NotACity";

        // Act
        Optional<String> result = gameService.saveCityImage(cityName);

        // Assert
        Assertions.assertFalse(result.isPresent());
    }

=======
>>>>>>> master
}

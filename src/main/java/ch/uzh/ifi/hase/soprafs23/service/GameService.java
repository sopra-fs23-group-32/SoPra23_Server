package ch.uzh.ifi.hase.soprafs23.service;

<<<<<<< HEAD
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.CityBase;
import ch.uzh.ifi.hase.soprafs23.entity.Round;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
=======
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
>>>>>>> said

/**
 * Game Service - The "worker", responsible for all functionality related to the game
 * (creates, modifies, deletes, finds). The result will be passed back to the caller.
 */

@Service
@Transactional
public class GameService {

<<<<<<< HEAD
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    private final UserRepository userRepository;
    private Game game;

    @Autowired
=======
    private final UserRepository userRepository;
    private Game game;

>>>>>>> said
    public GameService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

<<<<<<< HEAD
    public Game startNewGame(int rounds, int countdownTime, CityBase cityDB) {
        return new Game(rounds, countdownTime, cityDB);
=======


    public Game startNewGame(int rounds, int countdownTime, String category, int populationThreshold) {
        RandomCitiesService randomCitiesService = new RandomCitiesService();
        City city = randomCitiesService.getRandomCities(category,populationThreshold);
        this.game=new Game(rounds,countdownTime,city);

        return this.game;
>>>>>>> said
    }

    public void submitAnswers(List<Answer> answers) {
        game.submitAnswers(answers);
    }
<<<<<<< HEAD
=======
    public static Optional<String> saveCityImage(String cityName) {
        java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(GameService.class.getName());
        String ACCESS_KEY = "gy4-5Dl_v3J8NNPI_nYd8UL_0TIRB3XaCh4Ad1oqZW4";
        String UNSPLASH_API_ENDPOINT = "https://api.unsplash.com/search/photos?query=%s&per_page=1&orientation=landscape";

        try {
            // Build the API request URL
            String url = String.format(UNSPLASH_API_ENDPOINT, cityName);
            // Create HTTP client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Client-ID " + ACCESS_KEY)
                    .build();
            // Send the request and parse the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(responseBody);
            JsonNode photos = responseJson.get("results");
            // Check if any photos were found
            if (photos.isEmpty()) {
                LOGGER.warning("No photos found for " + cityName);
                return Optional.empty();
            }
            // Get the URL for the first photo and save it to disk
            JsonNode photo = photos.get(0);
            String photoUrl = photo.get("urls").get("regular").asText();
            String filename = cityName + ".jpg";
            String filePath = "C:\\Users\\a\\Desktop\\sopra\\no_git\\server\\src\\main\\java\\ch\\uzh\\ifi\\hase\\soprafs23\\static" + filename;
            URL urlObj = new URL(photoUrl);
            System.out.println((urlObj));
            try (InputStream inputStream = urlObj.openStream();
                 OutputStream outputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
            LOGGER.info("Saved image for " + cityName + " in static folder");
            return Optional.of(filename);
        }
        catch (IOException | InterruptedException e) {
            LOGGER.severe("Error fetching or saving image for " + cityName + ": " + e.getMessage());
            return Optional.empty();
        }
    }

>>>>>>> said

    public void addPlayers(List<Long> userIdList) {
        for (Long userId : userIdList) {
            User user = userRepository.findByUserId(userId);
            game.addPlayer(user);
        }
    }


<<<<<<< HEAD
}
=======
    public static void main(String[] args) {
        String cityName = "Zagreb";
        Optional<String> filename = saveCityImage(cityName);
        if (filename.isPresent()) {
            System.out.println("Saved image for " + cityName + " with filename: " + filename.get());
        }
        else {
            System.out.println("Failed to save image for " + cityName);
        }
    }
}
>>>>>>> said

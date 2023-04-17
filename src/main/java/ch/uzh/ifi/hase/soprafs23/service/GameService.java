package ch.uzh.ifi.hase.soprafs23.service;

<<<<<<< HEAD
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

=======
import ch.qos.logback.classic.Logger;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.SingleModeGameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
>>>>>>> master
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
<<<<<<< HEAD
=======
import java.util.Scanner;

import ch.uzh.ifi.hase.soprafs23.entity.City;
import java.util.ArrayList;
>>>>>>> master

/**
 * Game Service - The "worker", responsible for all functionality related to the game
 * (creates, modifies, deletes, finds). The result will be passed back to the caller.
 */

@Service
@Transactional
public class GameService {

    private final UserRepository userRepository;
<<<<<<< HEAD
    private Game game;

    public GameService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

=======
    private final SingleModeGameRepository singleModeGameRepository;
    private Game game;


    public GameService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
        this.singleModeGameRepository=singleModeGameRepository;
    }


>>>>>>> master
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }



    public Game startNewGame(int rounds, int countdownTime, String category, int populationThreshold) {
<<<<<<< HEAD
        RandomCitiesService randomCitiesService = new RandomCitiesService();
        City city = randomCitiesService.getRandomCities(category,populationThreshold);
        this.game=new Game(rounds,countdownTime,city);
=======
        try{
            List<String> city_names = getRandomCityNames(category,populationThreshold);
            this.game=new Game(rounds,countdownTime,city_names);
        }
        catch(Exception e){
            List<String> city_names=null;
            this.game=new Game(rounds,countdownTime,city_names);
        }
>>>>>>> master

        return this.game;
    }

    public void submitAnswers(List<Answer> answers) {
        game.submitAnswers(answers);
    }
<<<<<<< HEAD
    public static Optional<String> saveCityImage(String cityName) {
=======
    public   String saveCityImage(String cityName) {
>>>>>>> master
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
<<<<<<< HEAD
                return Optional.empty();
=======
                return "";
>>>>>>> master
            }
            // Get the URL for the first photo and save it to disk
            JsonNode photo = photos.get(0);
            String photoUrl = photo.get("urls").get("regular").asText();
            String filename = cityName + ".jpg";
<<<<<<< HEAD
            String filePath = "C:\\Users\\a\\Desktop\\sopra\\no_git\\server\\src\\main\\java\\ch\\uzh\\ifi\\hase\\soprafs23\\static" + filename;
            URL urlObj = new URL(photoUrl);
            System.out.println((urlObj));
=======
            String filePath = "C:\\Users\\a\\Desktop\\sopra_1604\\server\\src\\main\\java\\ch\\uzh\\ifi\\hase\\soprafs23\\static\\" + filename;
            URL urlObj = new URL(photoUrl);
>>>>>>> master
            try (InputStream inputStream = urlObj.openStream();
                 OutputStream outputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
            LOGGER.info("Saved image for " + cityName + " in static folder");
<<<<<<< HEAD
            return Optional.of(filename);
        }
        catch (IOException | InterruptedException e) {
            LOGGER.severe("Error fetching or saving image for " + cityName + ": " + e.getMessage());
            return Optional.empty();
        }
    }


=======
            System.out.println(photoUrl);
            return photoUrl;
        }
        catch (IOException | InterruptedException e) {
            LOGGER.severe("Error fetching or saving image for " + cityName + ": " + e.getMessage());
            return "";
        }
    }

    private static String getContinentCode(String category) {
        switch (category.toLowerCase()) {
            case "europe":
                return "EU";
            case "asia":
                return "AS";
            case "north america":
                return "NA";
            case "south america":
                return "SA";
            case "africa":
                return "AF";
            case "oceania":
                return "OC";
            case "world":
                return "";
            default:
                throw new IllegalArgumentException("Invalid continent category");
        }
    }

    public  List<String> getRandomCityNames(String category, int minPopulation) throws Exception {
        String baseUrl = "http://api.geonames.org/searchJSON";
        String username = "whowho";
        String continent = getContinentCode(category);
        String queryUrl = String.format("%s?continentCode=%s&featureClass=P&maxRows=1000&orderby=random&population>%d&username=%s", baseUrl, continent, minPopulation, username);
        
        URL url = new URL(queryUrl);
        Scanner scanner = new Scanner(url.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        scanner.close();
        
        List<String> cityNames = new ArrayList<>();
        JSONObject jsonObj = new JSONObject(response);
        JSONArray jsonArray = jsonObj.getJSONArray("geonames");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject cityObj = jsonArray.getJSONObject(i);
            String cityName = cityObj.getString("name");
            cityNames.add(cityName);
        }
    
        Collections.shuffle(cityNames); // shuffle the city names
    
        return cityNames.subList(0, Math.min(cityNames.size(), 5)); // return the first 5 (or fewer) city names
    }
    
    
>>>>>>> master
    public void addPlayers(List<Long> userIdList) {
        for (Long userId : userIdList) {
            User user = userRepository.findByUserId(userId);
            game.addPlayer(user);
        }
    }


<<<<<<< HEAD
    public static void main(String[] args) {
        String cityName = "Zagreb";
        Optional<String> filename = saveCityImage(cityName);
        if (filename.isPresent()) {
            System.out.println("Saved image for " + cityName + " with filename: " + filename.get());
        }
        else {
            System.out.println("Failed to save image for " + cityName);
=======
    public SingleModeGame getSingleModeGame(int gameId) {
        SingleModeGame singleModeGame=singleModeGameRepository.findByGameId(gameId);
        return singleModeGame;
        
    }


    public SingleModeGame startNewSingleModeGame(int player_id, int rounds, int countdownTime, String category, int populationThreshold) {
        try {
            List<String> cityNames = getRandomCityNames(category, populationThreshold);
            SingleModeGame game = new SingleModeGame();

            game.setTotalRounds(rounds);
            game.setCountdownTime(countdownTime);
            game.setPlayer(player_id);
            game.setSolutionCityName(cityNames.get(0));
            game.setCityOptions(cityNames);
            game.setGameEnded(false);
            game.setPlayerScore(0);
            game.setCurrentRound(1);
            game.setImageUrl(null);
            return game;
        } catch (Exception e) {
            SingleModeGame game = new SingleModeGame();
            game.setTotalRounds(rounds);
            game.setCountdownTime(countdownTime);
            game.setCityOptions(null);
            game.setGameEnded(false);
            game.setPlayerScore(0);
            game.setCurrentRound(1);
            game.setImageUrl(null);
            return game;
>>>>>>> master
        }
    }
}

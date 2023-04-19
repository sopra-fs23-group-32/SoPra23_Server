package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import org.json.JSONArray;
import java.io.FileOutputStream;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

/**
 * Game Service - The "worker", responsible for all functionality related to the game
 * (creates, modifies, deletes, finds). The result will be passed back to the caller.
 */

@Service
@Transactional
public class GameService {
    private Game game;
    private String CurrentLabel;

    public void createGame(CityCategory category, int rounds, int countdownTime) {
        int min_population=getRandomPopulationNumber();
        this.game = new Game(category, rounds, countdownTime);

        try{
            List<String> cityNames =  getRandomCityNames(category, min_population);
        }catch (Exception e){}
        this.game = new Game(category, rounds, countdownTime);
    }

    public void addPlayer(User user) {
        game.addPlayer(user);
    }

    public static int getRandomPopulationNumber(){
        int min=200000;
        int max=5000000;
        Random random=new Random();
        return random.nextInt(max-min+1)+min;
    }
    public Question goNextRound() {
        game.addCurrentRound();;
        CityCategory game_city_category=game.getCategory();
        int min_population=getRandomPopulationNumber();
        if(game.isGameEnded()){
            try{
                List<String> cityNames =  getRandomCityNames(game_city_category, min_population);
                String correctAnswer=cityNames.get(0);
                String option1=cityNames.get(1);
                String option2=cityNames.get(2);
                String option3=cityNames.get(3);
                String option4=cityNames.get(4);
                String pictureUrl=saveCityImage(correctAnswer);
                return new Question(option1, option2, option3, option4, pictureUrl);
            }catch (Exception e){
                List<String> cityNames=new ArrayList<String>();
                String correctAnswer="Zurich";
                String option1="Geneva";
                String option2="Basel";
                String option3="Lausanne";
                String option4="Berne";
                String pictureUrl=saveCityImage(correctAnswer);
                return new Question(option1, option2, option3, option4, pictureUrl);
            }            
        }
        List<String> citiesDrawn = game.getCityoptions();
        Random random = new Random();
        int intRand = random.nextInt(3);
        // Draw one city to generate picture
        CurrentLabel = citiesDrawn.get(0);
        String pictureUrl = "what?";
        // Others just return their name
        return new Question(citiesDrawn.get(0), citiesDrawn.get(2),
                citiesDrawn.get(3), citiesDrawn.get(4), pictureUrl);
    }
    /**
     * Add the answer to the player's list and update the points
     * @param playerId player's ID
     * @param answer an Answer object
     */
    public int submitAnswer(Long playerId, Answer answer) {
        Player currentPlayer = searchPlayerById(playerId);
        currentPlayer.addAnswer(answer.getAnswer());
        // get the right answer of current round
        int score = 0;
        String correctAnswer = CurrentLabel;
        if (answer.getAnswer().equals(correctAnswer)) {
            int remainingTime = game.getCountdownTime() - answer.getTimeTaken();
            score = calculateScore(Math.max(remainingTime, 0));
            currentPlayer.addScore(score);
        }
        return score;
    }

    private int calculateScore(int remainingTime) {
        // 50 pts for a correct answer and 10 pts for each second remains
        return 50 + (remainingTime * 10);
    }

    public Player searchPlayerById(Long playerId) {
        Iterator<Player> playerIterator = game.getPlayerList();
        while(playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if(Objects.equals(player.getUserId(), playerId)) {
                return player;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Player with ID %d was not found!\n", playerId));
    }

    public   String saveCityImage(String cityName) {
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
                return "";
            }
            // Get the URL for the first photo and save it to disk
            JsonNode photo = photos.get(0);
            String photoUrl = photo.get("urls").get("regular").asText();
            String filename = cityName + ".jpg";
            String filePath = "C:\\Users\\a\\Desktop\\sopra_1604\\server\\src\\main\\java\\ch\\uzh\\ifi\\hase\\soprafs23\\static\\" + filename;
            URL urlObj = new URL(photoUrl);
            try (InputStream inputStream = urlObj.openStream();
                 OutputStream outputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
            LOGGER.info("Saved image for " + cityName + " in static folder");
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



    public static List<String> getRandomCityNames(CityCategory category, int minPopulation) throws Exception {
        String baseUrl = "http://api.geonames.org/searchJSON";
        String username = "whowho";
        String continent_category=category.toString();
        String continent = getContinentCode(continent_category);
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
    
        return cityNames.subList(0, Math.min(cityNames.size(), 4)); // return the first 4 city names
    }


    //testing
    public static void main(String[] args){
        try{
            List<String> cityNames =  getRandomCityNames(CityCategory.EUROPE,2000000);
            System.out.println("Random city names: ");
            for (String cityName: cityNames){
                System.out.println(cityName);
            }
        }catch (Exception e){
            
            }
        }
    }


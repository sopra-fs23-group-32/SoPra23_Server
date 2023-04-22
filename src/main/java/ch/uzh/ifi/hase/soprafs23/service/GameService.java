package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final GameRepository gameRepository;
    private final Logger log = LoggerFactory.getLogger(GameService.class);

    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(Game newGame) {
        newGame.initGame();
        newGame = gameRepository.saveAndFlush(newGame);
        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }

    public void addPlayer(Long gameId, User userAsPlayer) {
        Game game = searchGameById(gameId);
        game.addPlayer(userAsPlayer);
    }

    public Question goNextRound(Long gameId) {
        System.out.println("Game Service Round reached");
        Game game = searchGameById(gameId);
        game.addCurrentRound();
        Question question=new Question("","","","","","");
        if(!game.isGameEnded()){
            try{
                List<String> cityNames = getRandomCityNames(game.getCategory(), getRandomPopulationNumber());
                Random random = new Random();
                int randInt = random.nextInt(3);
                String correctOption = cityNames.get(randInt);
                game.setCurrentAnswer(correctOption);
                String pictureUrl = getCityImage(correctOption);
                
                question= new Question(cityNames.get(0), cityNames.get(1), cityNames.get(2),cityNames.get(3), correctOption, pictureUrl);
                System.out.println("gameService question otpion1: ");
                System.out.println(question.getOption1());
                return question;

            }catch (Exception e){
                System.out.println("Unable to generate image");
                String option1="Geneva", option2="Basel", option3="Lausanne", option4="Bern";
                game.setCurrentAnswer(option4);
                String pictureUrl = getCityImage(option4);
                question= new Question(option1, option2, option3, option4, option4, pictureUrl);
                return question;
            }
        }
        return question;
    }

    public List<PlayerRanking> getRanking(Long gameId) {
        Game game = searchGameById(gameId);
        return game.getRanking();
    }

    public GameResult endGame(Long gameId) {
        Game game = searchGameById(gameId);
        if (!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Game has not finished yet!\n", gameId));
        }

        List<Player> winnerList = game.getWinners();
        List<PlayerRanking> playerRankingList = getRanking(gameId);

        GameResult gameResult = new GameResult(winnerList, playerRankingList);
        return gameResult;
    }

    // =============== all private non-service functions here =================
    public Game searchGameById(Long gameId) {
        checkIfIdExist(gameId);
        return gameRepository.findByGameId(gameId);
    }

    private void checkIfIdExist(Long gameId) {
        Game gameByGameId = gameRepository.findByGameId(gameId);
        if(gameByGameId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Game with ID %d was not found!\n", gameId));
        }
    }

    public Player searchPlayerById(Game game, Long playerId) {
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

    private static String getContinentCode(String category) {
        return switch (category.toLowerCase()) {
            case "europe" -> "EU";
            case "asia" -> "AS";
            case "north america" -> "NA";
            case "south america" -> "SA";
            case "africa" -> "AF";
            case "oceania" -> "OC";
            case "world" -> "";
            default -> throw new IllegalArgumentException("Invalid continent category");
        };
    }

    private static int getRandomPopulationNumber(){
        int min=200000;
        int max=5000000;
        Random random=new Random();
        return random.nextInt(max-min+1)+min;
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

    private String getCityImage(String cityName) {
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

    /**
     * Add the answer to the player's list and update the points
     * @param playerId player's ID
     * @param answer an Answer object
     */
    public int submitAnswer(Long gameId, Long playerId, Answer answer) {
        Game game = searchGameById(gameId);
        Player currentPlayer = searchPlayerById(game, playerId);
        currentPlayer.addAnswer(answer.getAnswer());
        // get the right answer of current round
        int score = 0;
        if (answer.getAnswer().equals(game.getCurrentAnswer())) {
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
}
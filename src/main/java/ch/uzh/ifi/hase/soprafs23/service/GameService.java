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

import java.nio.charset.StandardCharsets;
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
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }

    public void addPlayer(Long gameId, User userAsPlayer) {
        Game game = searchGameById(gameId);
        game.addPlayer(userAsPlayer);
    }

    public List<Long> getAllPlayers(Long gameId) {
        Game gameByGameId = gameRepository.findByGameId(gameId);
        List<Long> userIdList = new ArrayList<>();
        Iterator<Player> playerIterator = gameByGameId.getPlayerList();
        while(playerIterator.hasNext()) {
            userIdList.add(playerIterator.next().getUserId());
        }
        return userIdList;
    }

    public Question goNextRound(Long gameId) {
        System.out.println("Game Service - Round reached.");
        Game game = searchGameById(gameId);
        game.addCurrentRound();

        String option1="Geneva", option2="Basel", option3="Lausanne", option4="Bern";
        String pictureUrl = getCityImage(option4);
        Question question = new Question(option1, option2, option3, option4, option4, pictureUrl);
        if(!game.isGameEnded()){
            try{
                List<String> cityNames = getRandomCityNames(game.getCategory(), getRandomPopulationNumber());
                Random random = new Random();
                String correctOption = cityNames.get(random.nextInt(3));
                game.updateCurrentAnswer(correctOption);
                System.out.println("++++++\nCorrect Option: " + correctOption + "\n++++++");
                pictureUrl = getCityImage(correctOption);
                question= new Question(cityNames.get(0), cityNames.get(1),
                        cityNames.get(2),cityNames.get(3), correctOption, pictureUrl);
                System.out.println("Game Service - Question generated.");
            }catch (Exception e){
                System.out.println("Game Service - Unable to generate image");
                game.updateCurrentAnswer(option4);
            }
        }
        return question;
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

    public List<PlayerRanking> getRanking(Long gameId) {
        Game game = searchGameById(gameId);
        return game.getRanking();
    }

    public GameResult getGameResult(Long gameId) {
        Game game = searchGameById(gameId);
        if (!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Game with ID %d has not finished yet!\n", gameId));
        }
        return new GameResult(game.getWinners());
    }

    public void closeGame(Long gameId) {
        Game game = searchGameById(gameId);
        if(!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Game with ID %d has not finished yet!\n", gameId));
        }
        gameRepository.delete(game);
    }

    public void leaveGame(Long gameId, Long playerId) {
        Game game = searchGameById(gameId);
        game.deletePlayer(playerId);
    }
    // ======== Only invoke after ending the game and before deleting the game =========
    public GameInfo getGameInfo(Long gameId) {
        GameInfo gameInfo = new GameInfo();
        Game game = searchGameById(gameId);
        if(!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Game with ID %d has not finished yet!\n", gameId));
        }
        gameInfo.setGameId(gameId);
        gameInfo.setCategory(game.getCategory());
        gameInfo.setGameRounds(game.getTotalRounds());
        gameInfo.setPlayerNum(game.getPlayerNum());
        while (game.getLabelList().hasNext()) {
            gameInfo.addLabel(game.getLabelList().next());
        }
        return gameInfo;
    }

    public UserGameHistory getUserGameHistory(Long gameId, Long userId) {
        UserGameHistory userGameHistory = new UserGameHistory();
        Game game = searchGameById(gameId);
        if(!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Game with ID %d has not finished yet!\n", gameId));
        }
        Player player = searchPlayerById(game, userId);
        userGameHistory.setGameId(gameId);
        userGameHistory.setGameScore(player.getScore());
        while (player.getAnswerList().hasNext()) {
            userGameHistory.addAnswer(player.getAnswerList().next());
        }
        return userGameHistory;
    }

    // =============== all private non-service functions here =================

    public Game searchGameById(Long gameId) {
        checkIfGameIdExist(gameId);
        return gameRepository.findByGameId(gameId);
    }

    public void checkIfGameIdExist(Long gameId) {
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
            String.format("Player with ID %d was not found in Game with ID %d!\n",
                    playerId, game.getGameId()));
    }

    public int calculateScore(int remainingTime) {
        // 50 pts for a correct answer and 10 pts for each second remains
        return 20 + (remainingTime * 4);
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
        int min = 800000;
        int max = 8000000;
        int range = max - min;
        Random random = new Random();
        return random.nextInt(range/500000) * 500000 + min;
    }

    public static List<String> getRandomCityNames(CityCategory category, int minPopulation) throws Exception {
        String baseUrl = "http://api.geonames.org/searchJSON";
        String username = "whowho";
        String continent = getContinentCode(category.toString());
        String queryUrl = String.format(
                "%s?continentCode=%s&featureClass=P&maxRows=1000&orderby=random&population>%d&username=%s",
                baseUrl, continent, minPopulation, username);

        URL url = new URL(queryUrl);
        Scanner scanner = new Scanner(url.openStream(), StandardCharsets.UTF_8);
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
        // shuffle the city names
        Collections.shuffle(cityNames);
        // return the first 4 city names
        return cityNames.subList(0, Math.min(cityNames.size(), 4));
    }

    public String getCityImage(String cityName) {
        java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(GameService.class.getName());
        String ACCESS_KEY = "A1MN_Hj0S-MYeCZo4x2U4bfTYtyjYT9Am-WINbwGFCc";
        String UNSPLASH_API_ENDPOINT = "https://api.unsplash.com/search/photos?query=%s&per_page=1&orientation=landscape";
        try {
            // Build the API request URL
            String url = String.format(UNSPLASH_API_ENDPOINT, cityName);// ;
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
            String filePath = "../picture" + filename;
            URL urlObj = new URL(photoUrl);
            try (InputStream inputStream = urlObj.openStream();
                 OutputStream outputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
            LOGGER.info("Saved image for " + cityName + " in local folder");
            System.out.println(photoUrl);
            return photoUrl;
        }
        catch (IOException | InterruptedException e) {
            LOGGER.severe("Error fetching or saving image for " + cityName + ": " + e.getMessage());
            return "";
        }
    }
}


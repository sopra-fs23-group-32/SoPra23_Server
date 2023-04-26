package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.WebSocketType;

import ch.uzh.ifi.hase.soprafs23.entity.WebSocket;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.net.HttpURLConnection;
import java.net.URI;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.URL;
import java.net.URLEncoder;

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
    private final SimpMessagingTemplate messagingTemplate;


    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,SimpMessagingTemplate messagingTemplate) {
        this.gameRepository = gameRepository;
        this.messagingTemplate = messagingTemplate;

    }

    public Game createGame(Game newGame) {
        newGame.initGame();
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        log.debug("Created Information for Game: {}", newGame);
        updateGameStatus(newGame.getGameId(), WebSocketType.GAMESTATUSUPDATE, newGame.getCurrentStatus());
        return newGame;
    }

    public void addPlayer(Long gameId, User userAsPlayer) {
        Game game = searchGameById(gameId);
        game.addPlayer(userAsPlayer);
        updateGameStatus(gameId, WebSocketType.PLAYERUPDATE,game.getPlayerList());

    }

    public List<Long> getAllPlayers(Long gameId) {
        Game gameByGameId = gameRepository.findByGameId(gameId);
        List<Long> userIdList = new ArrayList<>();
        Set<Player> playerList = gameByGameId.getPlayerList();
        for(Player player : playerList) {
            userIdList.add(player.getUserId());
        }
        return userIdList;
    }
    



    public  Question  goNextRound(Long gameId) {
        System.out.println("Game Service - Round reached.");
        Game game = searchGameById(gameId);
        game.addCurrentRound();

        String option1="Geneva", option2="Basel", option3="Lausanne", option4="Bern";
        String pictureUrl="";
        Question question = new Question(option1, option2, option3, option4, option4, pictureUrl);
        if(!game.isGameEnded()){
            try{
                List<String> cityNames = getRandomCities(game.getCategory().toString());
                Random random = new Random();
                String correctOption = cityNames.get(random.nextInt(3));
                game.updateCurrentAnswer(correctOption);

                System.out.println("++++++\nCorrect Option: " + correctOption + "\n++++++");
                pictureUrl = getCityImage(correctOption);
                question= new Question(cityNames.get(0), cityNames.get(1),cityNames.get(2), cityNames.get(3), correctOption, pictureUrl);
                System.out.println("Question.cityNames "+cityNames.get(0)+cityNames.get(1)+cityNames.get(2)+cityNames.get(3)+cityNames.get(4)+cityNames.get(5));
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
        System.out.println("User: "+currentPlayer.getPlayerName()+" submitted the answer: "+answer.getAnswer());
        List<Player>playerList;
        currentPlayer.setHasAnswered(true);

        // get the right answer of current round
        int score = 0;
        if (answer.getAnswer().equals(game.getCurrentAnswer())) {
            int remainingTime = game.getCountdownTime() - answer.getTimeTaken();
            score = calculateScore(Math.max(remainingTime, 0));
            currentPlayer.addScore(score);
        }
        boolean hasAnswered=true;
        Set<Player> playerlist=game.getPlayerList();
        for(Player player: playerlist){
            if(!player.getHasAnswered()){
                hasAnswered=false;
                break;
            }
            if(hasAnswered){
                System.out.println("All users have answered");
                if(game.getCurrentRound()==game.getTotalRounds()){
                    game.setCurretnStatus(GameStatus.ENDED);
                }else{
                    game.setCurretnStatus(GameStatus.WAITINGINGAME);
                }
                updateGameStatus(game.getGameId(),WebSocketType.GAMESTATUSUPDATE, game.getCurrentStatus());
            }
        }
    gameRepository.saveAndFlush(game);
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



    public void updateGameStatus(Long gameId, WebSocketType webSocketType, Object webSocketParameter){
        try{
            WebSocket webSocket =new WebSocket(webSocketType, webSocketParameter);
            System.out.println("sent new gamestate to players, in game: "+gameId);
            messagingTemplate.convertAndSend("/instance/games/" + gameId, webSocket);
        }catch (Exception e){
            System.out.println("Error on updating gamestate to all players, game: "+gameId);
        }

    }


    public void updatePlayerStatus(Long playerId, long gameId, WebSocketType websocketType, Object webSocketParamaeter){
        try{
            WebSocket websocket=new WebSocket(websocketType, webSocketParamaeter);
            System.out.println("Updating playerstate to player "+playerId+" on game "+gameId);
            messagingTemplate.convertAndSend("/instance/games/" + gameId + "/" + playerId, websocketType);

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

   
    private static final int NUM_CITIES = 5;
    private static final int MIN_POPULATION = 1000000;

    public static List<String> getCountries(String continentCode) throws Exception {
        String apiUrl = "https://restcountries.com/v3.1/region/" + continentCode;
        URL countriesUrl = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) countriesUrl.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
        throw new Exception("Failed to fetch countries: " + responseCode);}

        BufferedReader reader =new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
        response.append(line);}
        reader.close();

        JSONArray countries = new JSONArray(response.toString());
        List<String> countryNames = new ArrayList<>();
        for (int i = 0; i < countries.length(); i++) {
        JSONObject country = countries.getJSONObject(i);
        if (country.get("name") instanceof JSONObject) {
            countryNames.add(country.getJSONObject("name").getString("common"));
        } else if (country.get("name") instanceof String) {
            countryNames.add(country.getString("name"));}}

        Collections.shuffle(countryNames);
        return countryNames;
    }

    public static List<String> getCities(String country) throws Exception {
        String url =
        "https://public.opendatasoft.com/api/records/1.0/search/?dataset=geonames-all-cities-with-a-population-1000&q=population%3E"
        + MIN_POPULATION
        + "&sort=population&facet=feature_code&facet=cou_name_en&facet=timezone&refine.cou_name_en="
        + URLEncoder.encode(country, StandardCharsets.UTF_8.toString())
        + "&rows=" + NUM_CITIES;
    
        URL citiesUrl = new URL(url);
        BufferedReader reader = new BufferedReader(new InputStreamReader(citiesUrl.openStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
    
        JSONObject responseJson = new JSONObject(response.toString());
        JSONArray records = responseJson.getJSONArray("records");
        List<String> cities = new ArrayList<>();
        for (int i = 0; i < records.length() && cities.size() < NUM_CITIES; i++) {
            JSONObject record = records.getJSONObject(i);
            if (record.getJSONObject("fields").getString("cou_name_en").equals(country)) {
                cities.add(record.getJSONObject("fields").getString("name"));
            }
        }
        Collections.shuffle(cities);
        return cities;
    }
    

    public static List<String> getRandomCities(String continentCode) throws Exception {
        List<String> allCities = new ArrayList<>();
        List<String> countries = getCountries(continentCode);
        countries.remove("Bosnia and Herzegovina");
    
        for (String country : countries) {
            try {
                List<String> cities = getCities(country);
                if (allCities.size() + cities.size() <= NUM_CITIES) {
                    allCities.addAll(cities);
                } else {
                    allCities.addAll(cities.subList(0, NUM_CITIES - allCities.size()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        Collections.shuffle(allCities);
        return allCities.subList(0, NUM_CITIES);
    }
    
      

  public static String getCityImage(String cityName) throws Exception {
    String searchUrl = "https://commons.wikimedia.org/w/api.php";
    String searchParams = String.format("action=query&format=json&list=search&srsearch=%s%%20skyline&srnamespace=6&srwhat=text&srlimit=1",URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString()));

    URL url = new URL(searchUrl + "?" + searchParams);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
    }

    in.close();
    connection.disconnect();

    JSONObject json = new JSONObject(content.toString());
    JSONObject query = json.getJSONObject("query");
    JSONArray search = query.getJSONArray("search");

    if (search.length() > 0) {
        JSONObject result = search.getJSONObject(0);
        String fileTitle = result.getString("title");
        String imageUrl = "https://commons.wikimedia.org/wiki/Special:FilePath/" + fileTitle.substring(5);
        return imageUrl;
    } else {
        return "";
    }
}
}

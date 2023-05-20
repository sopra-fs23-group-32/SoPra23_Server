package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WebSocketType;

import ch.uzh.ifi.hase.soprafs23.entity.WebSocket;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameInfoRepository;
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
import org.json.JSONArray;

import java.io.BufferedReader;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

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

    private final GameInfoRepository gameInfoRepository;
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final SimpMessagingTemplate messagingTemplate;


    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,SimpMessagingTemplate messagingTemplate, @Qualifier("gameInfoRepository") GameInfoRepository gameInfoRepository) {
        this.gameRepository = gameRepository;
        this.messagingTemplate = messagingTemplate;
        this.gameInfoRepository = gameInfoRepository;
    }

    public Game createGame(Game newGame) {
        newGame.initGame();
        newGame = gameRepository.save(newGame);
        gameRepository.flush();
        log.debug("Created Information for Game: {}", newGame);
        updateGameStatus(newGame.getGameId(), WebSocketType.GAME_INIT, newGame.getGameStatus());
        return newGame;
    }

    public List<Game> getAllGames() {
        List<Game> allGames = gameRepository.findAll();
        return  allGames;
    }

    public List<GameInfo> getAllGameInfos() {
        List<GameInfo> allGameInfos = gameInfoRepository.findAll();
        return allGameInfos;
    }

    public void addPlayer(Long gameId, User userAsPlayer) {
        Game game = searchGameById(gameId);
        Player newPlayer = new Player();
        newPlayer.setUserId(userAsPlayer.getUserId());
        newPlayer.setPlayerName(userAsPlayer.getUsername());
        newPlayer.setGame(game);
        game.addPlayer(newPlayer);
        updateGameStatus(gameId, WebSocketType.PLAYER_ADD, game.getGameStatus());
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
        if(game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Game with ID %d has ended!\n", gameId));
        }
        String option1="Geneva", option2="Basel", option3="Lausanne", option4="Bern";
        String pictureUrl = "https://images.unsplash.com/photo-1591128481965-d59b938e7db1?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=Mnw0NDQwMTF8MHwxfHNlYXJjaHwxfHxLbyVDNSVBMWljZSUyNTIwYnVpbGRpbmd8ZW58MHwwfHx8MTY4MzE0NjU1NA&ixlib=rb-4.0.3&q=80&w=1080";
        Question question = new Question(option1, option2, option3, option4, option4, pictureUrl);
        try{
            System.out.println("game getCategory " + game.getCategory());
            List<String> cityNames = getRandomCities(game.getCategory().toString());
            Random random = new Random();
            String correctOption = cityNames.get(random.nextInt(3));
            String citiImage = getCityImage(correctOption);
            if(citiImage != "")
                pictureUrl = citiImage;

            for (int j=0; j<4; j++) {
                game.setQuestions(j, cityNames.get(j));
            }
            game.updateCurrentAnswer(correctOption);
            System.out.println("game pictureURL." + pictureUrl);
            game.setImgUrl(pictureUrl);

            question= new Question(cityNames.get(0), cityNames.get(1),
                    cityNames.get(2),cityNames.get(3), correctOption, pictureUrl);
            System.out.println("Game Service - Question generated.");
        }catch (Exception e){
            System.out.println("Game Service - Unable to generate image");
            game.setQuestions(0, option1);
            game.setQuestions(1, option2);
            game.setQuestions(2, option3);
            game.setQuestions(3, option4);
            game.updateCurrentAnswer(option4);
            game.setImgUrl("");
        }

        game.addCurrentRound();
        game.setGameStatus(GameStatus.ANSWERING);
        updateGameStatus(gameId, WebSocketType.ROUND_UPDATE, game.getGameStatus());
        return question;
    }

    public Question getQuestions(Long gameId) {
        Game game = searchGameById(gameId);
        String option1, option2, option3, option4, correctA;
        String pictureUrl = game.getImgUrl();
        option1 = game.getQuestions(0);
        option2 = game.getQuestions(1);
        option3 = game.getQuestions(2);
        option4 = game.getQuestions(3);
        correctA = game.getCurrentAnswer();
        return new Question(option1, option2, option3, option4, correctA, pictureUrl);
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
        currentPlayer.setHasAnswered(true);

        // get the right answer of current round
        int score = 0;
        if (answer.getAnswer().equals(game.getCurrentAnswer())) {
            int remainingTime = game.getCountdownTime() - answer.getTimeTaken();
            score = calculateScore(remainingTime);
            currentPlayer.addScore(score);
        }
        return score;
    }

    public boolean checkIfAllAnswered(Long gameId) {
        boolean allAnswered = true;
        Game game = searchGameById(gameId);
        Iterator<Player> playerList = game.getPlayerList();
        while(playerList.hasNext()){
            if(!playerList.next().getHasAnswered()){
                allAnswered = false;
                break;
            }
        }
        if(allAnswered){
            game.setGameStatus(GameStatus.WAITING);
            playerList = game.getPlayerList();
            while(playerList.hasNext()) {
                playerList.next().setHasAnswered(false);
            }
        }
        updateGameStatus(gameId, WebSocketType.ANSWER_UPDATE, game.getGameStatus());
        return allAnswered;
    }

    public List<PlayerRanking> getRanking(Long gameId) {
        Game game = searchGameById(gameId);
        return game.getRanking();
    }

    public List<String> getGameResult(Long gameId) {
        Game game = searchGameById(gameId);
        if (!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Game with ID %d has not finished yet!\n", gameId));
        }
        return game.getWinners();
    }

    public void closeGame(Long gameId) {
        Game game = searchGameById(gameId);
        game.setGameStatus(GameStatus.ENDED);
        updateGameStatus(gameId, WebSocketType.GAME_END, game.getGameStatus());
    }

    public void leaveGame(Long gameId, Long playerId) {
        Game game = searchGameById(gameId);
        game.deletePlayer(playerId);
        updateGameStatus(gameId, WebSocketType.PLAYRE_REMOVE, game.getGameStatus());
    }


    // ======== Only invoke after ending the game and before deleting the game =========
    public GameInfo getGameInfo(Long gameId) {
        GameInfo gameInfo = new GameInfo();
        Game game = searchGameById(gameId);

        gameInfo.setGameId(gameId);
        gameInfo.setCategory(game.getCategory());
        gameInfo.setGameRounds(game.getTotalRounds());
        gameInfo.setPlayerNum(game.getPlayerNum());
        Iterator<String> labelList = game.getLabelList();
        while (labelList.hasNext()) {
            gameInfo.addLabel(labelList.next());
        }
        return gameInfo;
    }

    public UserGameHistory getUserGameHistory(Long gameId, Long userId) {
        UserGameHistory userGameHistory = new UserGameHistory();
        Game game = searchGameById(gameId);
        Player player = searchPlayerById(game, userId);
        userGameHistory.setGameId(gameId);
        userGameHistory.setGameScore(player.getScore());
        Iterator<String> answerList = player.getAnswerList();
        while (answerList.hasNext()) {
            userGameHistory.addAnswer(answerList.next());
        }
        return userGameHistory;
    }

    // ================ functions for web socket =======================
    public void updateGameStatus(Long gameId, WebSocketType webSocketType, Object webSocketParameter){
        try{
            WebSocket webSocket =new WebSocket(webSocketType, webSocketParameter);
            System.out.printf("Sending new state of gameID %d to players - %s\n", gameId, webSocketParameter.toString());
            messagingTemplate.convertAndSend("/instance/games/" + gameId, webSocket);
        }catch (Exception e){
            System.out.printf("Error on updating state of gameID %d to all players\n", gameId);
        }
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
    private static final int MIN_POPULATION = 100000;

    public static List<String> getCountries(String continentCode) throws Exception {
        String apiUrl = "https://restcountries.com/v3.1/";
        String continentCode1;
        if(continentCode == "NORTH_AMERICA" ) continentCode1 = "region/NORTH%20AMERICA";
        else if (continentCode == "SOUTH_AMERICA") continentCode1 = "region/SOUTH%20AMERICA";
        else if (continentCode == "WORLD") continentCode1 = "all";
        else continentCode1 = "region/" + continentCode;
        apiUrl += continentCode1;

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

        if(countryNames.size() < NUM_CITIES) return countryNames;

        Random random = new Random();
        int randomIndex = random.nextInt(countryNames.size() - NUM_CITIES);
        return countryNames.subList(randomIndex, randomIndex + NUM_CITIES);
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

        String endPoint = "https://api.unsplash.com/search/photos";
        String accessKey = "n_44tTFqKgUUalZYtv2UTmP-3rNunH-zak0X7yBgS8o";

      String searchParams = String.format("query=%s&orientation=landscape&per_page=1&&client_id=%s",
              URLEncoder.encode(cityName + "+city", StandardCharsets.UTF_8.toString()), accessKey);
//    String searchParams = String.format(
//            "action=query&format=json&list=search&srsearch=%s%%20skyline&srnamespace=6&srwhat=text&srlimit=1",
//            URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString()));

    URL url = new URL(endPoint + "?" + searchParams);
    System.out.println("image" + url);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
    }

    in.close();
    connection.disconnect();

    System.out.println(connection.toString());

    JSONObject json = new JSONObject(content.toString());
//    JSONObject query = json.getJSONObject("results");
    JSONArray results = json.getJSONArray("results");
    System.out.println(results.length());
    if (results.length() > 0) {
        JSONObject result = results.getJSONObject(0);
        JSONObject urls = result.getJSONObject("urls");
        String fileTitle = urls.getString("regular");
        System.out.println(fileTitle);
        return fileTitle;
//        return "https://commons.wikimedia.org/wiki/Special:FilePath/" + fileTitle.substring(5);
    }
    else {
        return "";
    }
}
}

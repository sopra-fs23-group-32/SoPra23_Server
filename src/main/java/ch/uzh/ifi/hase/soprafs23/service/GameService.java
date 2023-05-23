package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
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
import org.json.JSONArray;

import java.io.BufferedReader;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.Math.min;

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
        System.out.printf("--> Created Game %d.", newGame.getGameId());
        return newGame;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public void addPlayer(Long gameId, User userAsPlayer) {
        Game game = searchGameById(gameId);
        Player newPlayer = new Player();
        newPlayer.setUserId(userAsPlayer.getUserId());
        newPlayer.setPlayerName(userAsPlayer.getUsername());
        newPlayer.setGame(game);
        game.addPlayer(newPlayer);
        updateGameStatus(gameId, WebSocketType.PLAYER_ADD, game.getGameStatus());
        System.out.println("--> Player added: " + newPlayer.getPlayerName());
    }

    public List<Long> getAllPlayers(Long gameId) {
        Game gameByGameId = gameRepository.findByGameId(gameId);
        List<Long> userIdList = new ArrayList<>();
        Iterator<Player> playerIterator = gameByGameId.getPlayerList();
        while (playerIterator.hasNext()) {
            userIdList.add(playerIterator.next().getUserId());
        }
        return userIdList;
    }

    public Question goNextRound(Long gameId) {
        Game game = searchGameById(gameId);
        if(game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                String.format("Game with ID %d has ended!\n", gameId));
        }
        System.out.printf("--------> Game %d - Round %d reached.\n", gameId, game.getCurrentRound()+1);

        String option1="Geneva", option2="Basel", option3="Lausanne", option4="Bern";
        String defaultPicUrl="https://images.unsplash.com/photo-1591128481965-d59b938e7db1?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=Mnw0NDQwMTF8MHwxfHNlYXJjaHwxfHxLbyVDNSVBMWljZSUyNTIwYnVpbGRpbmd8ZW58MHwwfHx8MTY4MzE0NjU1NA&ixlib=rb-4.0.3&q=80&w=1080";
        Question question = new Question(option1, option2, option3, option4, option4, defaultPicUrl);
        Random random = new Random();
        String pictureUrl = "";
        String correctOption = null;
        List<String> cityNames = null;
        // get 30 cities
        List<String> selectedCities = getRandomCities(game.getCategory());
        // remove cities have shown
        Iterator<String> shownCityList = game.getLabelList();
        while (shownCityList.hasNext()) {
            selectedCities.remove(shownCityList.next());
        }

        try{
            int try_count = 0, max_try = 5;
            while(pictureUrl.equals("") && try_count < max_try) {
                try_count ++;
                // shuffle and pick 4cities
                Collections.shuffle(selectedCities);
                cityNames = selectedCities.subList(0, 4);
                // pick one city as the correct answer
                correctOption = cityNames.get(random.nextInt(4));
                System.out.println("Generating Img for City:" + correctOption);
                pictureUrl = getCityImage(correctOption);
            }

            for (int j=0; j<4; j++) {game.setQuestions(j, cityNames.get(j));}
            game.updateCurrentAnswer(correctOption);
            game.setImgUrl(pictureUrl);

            System.out.println(pictureUrl);
            question= new Question(cityNames.get(0), cityNames.get(1),
                cityNames.get(2),cityNames.get(3), correctOption, pictureUrl);
        }
        catch (Exception e){
            System.out.printf("--------> Game %d - Unable to generate image.\n", gameId);
            game.setQuestions(0, option1);
            game.setQuestions(1, option2);
            game.setQuestions(2, option3);
            game.setQuestions(3, option4);
            game.updateCurrentAnswer(option4);
            game.setImgUrl(defaultPicUrl);
        }
        game.addCurrentRound();
        game.setGameStatus(GameStatus.ANSWERING);
        updateGameStatus(gameId, WebSocketType.ROUND_UPDATE, game.getGameStatus());
        System.out.printf("---> Game %d - Question generated.\n", gameId);
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

//    public void updateCurrentImage(Game game, String ImgUrl) {
//
//    }

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
            currentPlayer.addCorrectCount();
        }
        return score;
    }

    public boolean checkIfAllAnswered(Long gameId) {
        boolean allAnswered = true;
        Game game = searchGameById(gameId);
        Iterator<Player> playerList = game.getPlayerList();
        while (playerList.hasNext()){
            if (!playerList.next().getHasAnswered()){
                allAnswered = false;
                break;
            }
        }
        if (allAnswered){
            game.setGameStatus(GameStatus.WAITING);
            updateGameStatus(gameId, WebSocketType.ALL_ANSWER, game.getGameStatus());
            playerList = game.getPlayerList();
            while (playerList.hasNext()) {
                playerList.next().setHasAnswered(false);
            }
        }
        return allAnswered;
    }

    public List<PlayerRanking> getRanking(Long gameId) {
        Game game = searchGameById(gameId);
        return game.getRanking();
    }

    public List<String> getGameResult(Long gameId) {
        Game game = searchGameById(gameId);
        if (!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                String.format("Game with ID %d has not finished yet!\n", gameId));
        }
        return game.getWinners();
    }

    public void closeGame(Long gameId) {
        Game game = searchGameById(gameId);
        game.setGameStatus(GameStatus.DELETED);
        updateGameStatus(gameId, WebSocketType.GAME_DELETED, game.getGameStatus());
        gameRepository.delete(game);
    }

    public void leaveGame(Long gameId, Long playerId) {
        Game game = searchGameById(gameId);
        // remove player from the player list
        game.deletePlayer(playerId);
		updateGameStatus(gameId, WebSocketType.PLAYER_REMOVE, game.getGameStatus());
    }


    // ======================= functions for game history =======================
    // ======== Only invoke after ending the game and before deleting the game =========
    public GameInfo getGameInfo(Long gameId) {
        GameInfo gameInfo = new GameInfo();
        Game game = searchGameById(gameId);
        if(!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                String.format("Game with ID %d has not finished yet!\n", gameId));
        }
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
        if(!game.isGameEnded()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                String.format("Game with ID %d has not finished yet!\n", gameId));
        }
        // First invoked by the host, to make sure the GameInfo has existed
        if(game.getGameStatus() != GameStatus.ENDED && game.getGameStatus() != GameStatus.DELETED) {
            game.setGameStatus(GameStatus.ENDED);
            updateGameStatus(gameId, WebSocketType.GAME_END, game.getGameStatus());
        }
        Player player = searchPlayerById(game, userId);
        userGameHistory.setGameId(gameId);
        userGameHistory.setGameScore(player.getScore());
        userGameHistory.setCorrectRate(player.getCorrectRate() * 100.0f);
        Iterator<String> answerList = player.getAnswerList();
        while (answerList.hasNext()) {
            userGameHistory.addAnswer(answerList.next());
        }
        return userGameHistory;
    }

    // ======================= functions for web socket =======================
    public void updateGameStatus(Long gameId, WebSocketType webSocketType, Object webSocketParameter){
        try {
            WebSocket webSocket = new WebSocket(webSocketType, webSocketParameter);
            System.out.printf("----> Game(ID %d) - %s, Socket - %s\n",
                gameId, webSocketParameter.toString(), webSocketType.toString());
            messagingTemplate.convertAndSend("/instance/games/" + gameId, webSocket);
        } catch (Exception e){
            System.out.printf("Error on updating state of gameID %d to all players\n", gameId);
        }
    }

    // =============== all non-service functions here =================
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

    private static final int NUM_COUNTRY = 10;
    private static final int CITIES_PER_COUNTRY = 6;
    private static final int NUM_CITIES = 10;
    private static final int MIN_POPULATION = 700000;

    public static final Map<CityCategory, List<String>> COUNTRIES_BY_CONTINENT = new HashMap<>();
    // all countries' name divided by category
    static {
        COUNTRIES_BY_CONTINENT.put(CityCategory.OCEANIA, Arrays.asList("Australia", "Fiji", "Nauru", "New Zealand", "Palau", "Papua New Guinea", "Tonga"));
        COUNTRIES_BY_CONTINENT.put(CityCategory.NORTH_AMERICA, Arrays.asList("United States", "Canada", "Mexico", "Guatemala", "Haiti",
            "Dominican Republic", "Cuba", "Honduras", "Nicaragua", "El Salvador", "Costa Rica", "Panama", "Jamaica",
            "Trinidad and Tobago", "Barbados", "Dominica"));
        COUNTRIES_BY_CONTINENT.put(CityCategory.SOUTH_AMERICA, Arrays.asList("Argentina", "Bolivia", "Brazil", "Chile", "Colombia", "Ecuador",
            "Guyana", "Paraguay", "Peru", "Suriname", "Uruguay", "Venezuela"));
        COUNTRIES_BY_CONTINENT.put(CityCategory.AFRICA, Arrays.asList("Algeria", "Angola", "Benin", "Botswana", "Burkina Faso", "Burundi",
            "Cape Verde", "Cameroon", "Central African Republic", "Chad", "Congo", "Egypt", "Equatorial Guinea", "Eritrea", "Somalia", "South Africa",
            "Ethiopia", "Gabon", "Gambia", "Ghana", "Guinea", "Kenya", "Lesotho", "Liberia", "Libya", "Madagascar", "Malawi", "Tanzania", "Togo", "Tunisia", "Uganda",
            "Mauritania", "Mauritius", "Morocco", "Mozambique", "Namibia", "Niger", "Nigeria", "Rwanda", "Senegal", "Sierra Leone", "Mali", "South Sudan",
            "Sudan", "Zambia", "Zimbabwe"));
        COUNTRIES_BY_CONTINENT.put(CityCategory.EUROPE, Arrays.asList("Albania", "Austria", "Belarus", "Belgium", "Bulgaria", "Croatia", "Ireland",
            "Cyprus", "Czech Republic", "Denmark", "Estonia", "Finland", "France", "Germany", "Greece", "Hungary", "Iceland", "Netherlands",
            "Italy", "Latvia", "Lithuania", "Luxembourg", "Moldova", "Montenegro", "North Macedonia", "Norway", "Poland", "Portugal", "Romania",
            "Russia", "Serbia", "Slovakia", "Slovenia", "Spain", "Sweden", "Switzerland", "Ukraine"));
        COUNTRIES_BY_CONTINENT.put(CityCategory.ASIA, Arrays.asList("Afghanistan", "Armenia", "Azerbaijan", "Bahrain", "Bangladesh",
            "Cambodia", "China", "Cyprus", "Georgia", "India", "Indonesia", "Iran", "Iraq", "Israel", "Japan", "Jordan", "Kuwait",
            "Laos", "Lebanon", "Malaysia", "Mongolia", "Nepal", "Oman", "Pakistan", "Philippines", "Qatar", "Turkey", "United Arab Emirates",
            "Saudi Arabia", "Singapore", "South Korea", "Sri Lanka", "Syria", "Tajikistan", "Thailand", "Uzbekistan", "Vietnam", "Yemen"));
//        List<String> worldCountries = new ArrayList<>();
//        worldCountries.addAll(COUNTRIES_BY_CONTINENT.get("AFRICA"));
//        worldCountries.addAll(COUNTRIES_BY_CONTINENT.get("ASIA"));
//        worldCountries.addAll(COUNTRIES_BY_CONTINENT.get("EUROPE"));
//        worldCountries.addAll(COUNTRIES_BY_CONTINENT.get("NORTH_AMERICA"));
//        worldCountries.addAll(COUNTRIES_BY_CONTINENT.get("OCEANIA"));
//        worldCountries.addAll(COUNTRIES_BY_CONTINENT.get("SOUTH_AMERICA"));
//        COUNTRIES_BY_CONTINENT.put("WORLD", worldCountries);
    }

//    public static List<String> getCountries(String continentCode) throws Exception {
//        String continentCode1 = switch (continentCode) {
//            case "NORTH_AMERICA" -> "region/NORTH%20AMERICA";
//            case "SOUTH_AMERICA" -> "region/SOUTH%20AMERICA";
//            default -> "region/" + continentCode;
//        };
//        URL countriesUrl = new URL("https://restcountries.com/v3.1/" + continentCode1);
//        HttpURLConnection connection = (HttpURLConnection) countriesUrl.openConnection();
//        connection.setRequestMethod("GET");
//        int responseCode = connection.getResponseCode();
//        if (responseCode != 200) {
//            throw new Exception("Failed to fetch countries: " + responseCode);
//        }
//
//        BufferedReader reader =new BufferedReader(
//            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
//        );
//        StringBuilder response = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {response.append(line);}
//        reader.close();
//        connection.disconnect();
//
//        JSONArray countryList = new JSONArray(response.toString());
//        List<String> countryNames = new ArrayList<>();
//        for (int i = 0; i < countryList.length(); i++) {
//            JSONObject country = countryList.getJSONObject(i);
//            if (country.get("name") instanceof JSONObject) {
//                countryNames.add(country.getJSONObject("name").getString("common"));
//            }
//            else if (country.get("name") instanceof String) {
//                countryNames.add(country.getString("name"));
//            }
//        }
//        for(String country : countryNames){System.out.print(country + ", ");}
//        System.out.println("\n--> Successfully get countries list.");
////        if(countryNames.size() < NUM_COUNTRY) {return countryNames;}
////        return countryNames.subList(0, NUM_COUNTRY);
//        return countryNames;
//    }

    public static List<String> getCities(String country) throws Exception {
        String url = "https://public.opendatasoft.com/api/records/1.0/search/"
            + "?dataset=geonames-all-cities-with-a-population-1000"
            + "&sort=population&q=population>" + MIN_POPULATION
            + "&facet=feature_code&facet=cou_name_en&facet=timezone"
            + "&refine.cou_name_en=" + URLEncoder.encode(country, StandardCharsets.UTF_8.toString())
            + "&rows=" + CITIES_PER_COUNTRY;
        URL citiesUrl = new URL(url);

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(citiesUrl.openStream(), StandardCharsets.UTF_8)
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {response.append(line);}
        reader.close();
    
        JSONObject responseJson = new JSONObject(response.toString());
        JSONArray records = responseJson.getJSONArray("records");
        List<String> cities = new ArrayList<>();
        for (int i = 0; i < records.length() && cities.size() <= CITIES_PER_COUNTRY; i++) {
            JSONObject record = records.getJSONObject(i).getJSONObject("fields");
            if (record.getString("cou_name_en").equals(country)) {
                cities.add(record.getString("name"));
            }
        }
        Collections.shuffle(cities);
        return cities;
    }

    public static List<String> getRandomCities(CityCategory category){
        List<String> countryList = COUNTRIES_BY_CONTINENT.get(category);
        Collections.shuffle(countryList);
        countryList = countryList.subList(0, min(countryList.size(), NUM_COUNTRY));


        List<String> selectedCities = new ArrayList<>();
        for (String country : countryList) {
            try {
                List<String> cities = getCities(country);
//                System.out.println("-------> Cities in:" + country);
//                for(String city : cities) {System.out.println(city);}
                selectedCities.addAll(cities);
                if(selectedCities.size() >= NUM_CITIES) {
                    break;
                }
            }
            catch (Exception e) {e.printStackTrace();}
        }
        return selectedCities;
    }

    public static String getCityImage(String cityName) throws Exception {
        String endPoint = "https://api.unsplash.com/search/photos";
        String accessKey = "n_44tTFqKgUUalZYtv2UTmP-3rNunH-zak0X7yBgS8o";
        String searchParams = "?query="+ URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString())
            + "&per_page=1&client_id=" + accessKey;
        URL url = new URL(endPoint + searchParams);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Failed to fetch picture: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {response.append(line);}
        reader.close();
        connection.disconnect();

        JSONObject json = new JSONObject(response.toString());
        JSONArray results = json.getJSONArray("results");
        if (results.length() > 0) {
            JSONObject urls = results.getJSONObject(0).getJSONObject("urls");
            return urls.getString("regular");
        }
        System.out.printf("---> No results for '%s'.", cityName);
        return "";
    }

    public static String refreshImage(String cityName) throws Exception{
        Random random = new Random();
        int randomPage = (random.nextInt(10));
        String endPoint = "https://api.unsplash.com/search/photos";
        String accessKey = "n_44tTFqKgUUalZYtv2UTmP-3rNunH-zak0X7yBgS8o";
        // &orientation=landscape
        String searchParams = "?query="+ URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString())
                + "&page="+randomPage + "&per_page=10&client_id="+accessKey;
        URL url = new URL(endPoint + searchParams);
        //System.out.println(url);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {response.append(line);}
        reader.close();
        connection.disconnect();

        JSONObject json = new JSONObject(response.toString());
        JSONArray results = json.getJSONArray("results");
        if (results.length() > 0) {
            int randomImage = (random.nextInt(results.length()));
            JSONObject urls = results.getJSONObject(randomImage).getJSONObject("urls");
            return urls.getString("regular");
        }
        System.out.printf("---> No results for '%s'.", cityName);
        return "";
    }
}

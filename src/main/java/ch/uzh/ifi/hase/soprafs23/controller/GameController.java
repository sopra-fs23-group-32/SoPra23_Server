package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.SingleModeGame;
import ch.uzh.ifi.hase.soprafs23.service.GameService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User Controller - Responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution
 * to the UserService and finally return the result.
 */
@RestController
public class GameController {

    private final GameService gameService;


    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Start the game
     * @param rounds The number of rounds
     * @param countdownTime The countdown time of each round
     * @param cityDB The city database
     */


    //get 5 city options with the right cityname=first index val
     @GetMapping("/random-cities")
     public List<String> getRandomCities(@RequestParam String category, @RequestParam int populationThreshold) {
        System.out.println("wird erreicht"); 
        System.out.println("POPULATION: "+populationThreshold);
        try{
            List<String> city_names= gameService.getRandomCityNames(category, populationThreshold);
            return city_names;
        }
        catch(Exception e){
            List<String> city_names=null;
            return city_names;
        }
        
        
     }
     @PostMapping("/city-image")
     public String saveCityImage(@RequestBody Map<String, String> request) {
         String cityName = request.get("cityName");
         String imageUrl = gameService.saveCityImage(cityName);
         return imageUrl;
     }
     
     @PostMapping("/singlemode/start")
    public SingleModeGame startSingleModeGame(@RequestParam Player player,
                                        @RequestParam int rounds,
                                        @RequestParam int countdownTime,
                                        @RequestParam String category,
                                        @RequestParam int populationThreshold) {
        return startSingleModeGame(player,rounds, countdownTime, category, populationThreshold);
    }


    @GetMapping("/singlemode/{gameId}")
    public SingleModeGame getSingleModegame(@RequestParam int gameId) {
        return gameService.getSingleModeGame(gameId);
    }




    /**
     * Submit the answers of players
     * @param answers The list of answers, including the timeTaken and answer of each player
     */
    @PostMapping("/game/answers")
    @ResponseStatus(HttpStatus.OK)
    public void submitAnswers(@RequestBody List<Answer> answers) {
        gameService.submitAnswers(answers);
    }

}

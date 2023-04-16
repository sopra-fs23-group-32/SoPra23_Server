package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.City;
import ch.uzh.ifi.hase.soprafs23.service.RandomCitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RandomCitiesController {

    private final RandomCitiesService randomCitiesService;

    @Autowired
    public RandomCitiesController(RandomCitiesService randomCitiesService) {
        this.randomCitiesService = randomCitiesService;
    }

    @GetMapping("/random-cities")
    public City getRandomCity(@RequestParam String category, @RequestParam int populationThreshold) {
        return randomCitiesService.getRandomCities(category, populationThreshold);
    }
}

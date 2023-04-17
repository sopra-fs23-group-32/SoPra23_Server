package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.City;
import ch.uzh.ifi.hase.soprafs23.service.RandomCitiesService;
import org.springframework.web.bind.annotation.*;


@RestController
public class RandomCitiesController {

    private final RandomCitiesService randomCitiesService;

    public RandomCitiesController(RandomCitiesService randomCitiesService) {
        this.randomCitiesService = randomCitiesService;
    }

    @GetMapping("/random-cities")
    public City getRandomCity(@RequestParam String category, @RequestParam int populationThreshold) {
        return randomCitiesService.getRandomCities(category, populationThreshold);
    }
}

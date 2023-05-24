package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserRanking;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserRankingGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.ScoreBoardService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.UserStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class ScoreBoardController {

    private final ScoreBoardService scoreBoardService;

    ScoreBoardController(ScoreBoardService scoreBoardService) {
        this.scoreBoardService = scoreBoardService;
    }

    /**
     * Get all users' ranking
     * @param category category of the cities
     * @return List of sorted User w.r.t their total score
     */
    @GetMapping("/users/ranking")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserRankingGetDTO> getUserRanking(
            @RequestParam(name = "category", required = false) CityCategory category) {
        System.out.println("Acquire for ranking in: " + category);

        List<UserRanking> userRankingList = scoreBoardService.getUserRanking(category);

        List<UserRankingGetDTO> userRankingGetDTOList = new ArrayList<>();
        for (UserRanking userRanking : userRankingList) {
            userRankingGetDTOList.add(DTOMapper.INSTANCE.convertEntityToUserRankingGetDTO(userRanking));
        }

        return userRankingGetDTOList;
    }
}
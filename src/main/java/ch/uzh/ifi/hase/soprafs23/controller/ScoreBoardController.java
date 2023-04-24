package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserRanking;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserRankingGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.UserStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
public class ScoreBoardController {

    private final UserService userService;
    private final UserStatisticsService userStatisticsService;

    ScoreBoardController(UserService userService, UserStatisticsService userStatisticsService) {
        this.userService = userService;
        this.userStatisticsService = userStatisticsService;
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
        List<User> userList = userService.getUsers();
        List<Long> userIdList = new ArrayList<>();
        for(User user:userList) {
            userIdList.add(user.getUserId());
        }

        List<Long> sortedUserIdList = userIdList.stream().sorted(
            Comparator.comparing(userStatisticsService::getUserTotalScore)).toList();
        if(category != null) {
            sortedUserIdList = userIdList.stream().sorted(
                Comparator.comparing(
                    userId -> userStatisticsService.getUserSpecificScore(userId, category))
                ).toList();
        }

        List<UserRankingGetDTO> userGetDTOList = new ArrayList<>();
        for(Long userId:sortedUserIdList) {
            User user = userService.searchUserById(userId);
            UserRanking userRanking = new UserRanking(
                user.getUserId(), user.getUsername(), user.getCreateDay(),
                userStatisticsService.getUserTotalScore(userId),
                userStatisticsService.getUserTotalGameNum(userId)
            );
            if(category != null) {
                userRanking = new UserRanking(
                    user.getUserId(), user.getUsername(), user.getCreateDay(),
                    userStatisticsService.getUserSpecificScore(userId, category),
                    userStatisticsService.getUserSpecificGameNum(userId, category)
                );
            }
            userGetDTOList.add(DTOMapper.INSTANCE.convertEntityToUserRankingGetDTO(userRanking));
        }
        return userGetDTOList;
    }
}

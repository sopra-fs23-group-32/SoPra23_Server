package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameInfoRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class ScoreBoardServiceTest {
  @Mock
  private UserStatisticsRepository userStatisticsRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private ScoreBoardService scoreBoardService;
  @InjectMocks
  private UserService userService;
  @InjectMocks
  private UserStatisticsService userStatisticsService;
  private SimpMessagingTemplate messagingTemplate;

  @BeforeEach
  public void setup() {
      MockitoAnnotations.openMocks(this);
      scoreBoardService = new ScoreBoardService(userService, userStatisticsService, messagingTemplate);

      // given
      Long userId1 = 1L;
      Long userId2 = 2L;
      Long userId3 = 3L;

      User user1 = new User();
      user1.setUserId(userId1);
      user1.setUsername("user1");
      user1.setCreateDay(new Date());
      User user2 = new User();
      user2.setUserId(userId2);
      user2.setUsername("user2");
      user2.setCreateDay(new Date());
      User user3 = new User();
      user3.setUserId(userId3);
      user3.setUsername("user3");
      user3.setCreateDay(new Date());
      List<User> userList = new ArrayList<>(Arrays.asList(user1, user2, user3));

      UserStatistics userStatistics1 = new UserStatistics();
      userStatistics1.initUserStatistics();
      userStatistics1.setUserId(userId1);
      userStatistics1.updateScore(100, CityCategory.ASIA);
      userStatistics1.updateGameNum(CityCategory.ASIA);
     
      UserStatistics userStatistics2 = new UserStatistics();
      userStatistics2.initUserStatistics();
      userStatistics2.setUserId(userId2);
      userStatistics2.updateScore(60, CityCategory.ASIA);
      userStatistics2.updateGameNum(CityCategory.ASIA);
      UserStatistics userStatistics3 = new UserStatistics();
      userStatistics3.initUserStatistics();
      userStatistics3.setUserId(userId3);
      userStatistics3.updateScore(120, CityCategory.ASIA);
      userStatistics3.updateGameNum(CityCategory.ASIA);

      // when -> any object is being found in the UserStatisticsRepository -> return the dummy
      Mockito.when(userRepository.findAll()).thenReturn(userList);
      Mockito.when(userRepository.findByUserId(Mockito.eq(1L))).thenReturn(user1);
      Mockito.when(userRepository.findByUserId(Mockito.eq(2L))).thenReturn(user2);
      Mockito.when(userRepository.findByUserId(Mockito.eq(3L))).thenReturn(user3);
      Mockito.when(userStatisticsRepository.findByUserId(Mockito.eq(1L))).thenReturn(userStatistics1);
      Mockito.when(userStatisticsRepository.findByUserId(Mockito.eq(2L))).thenReturn(userStatistics2);
      Mockito.when(userStatisticsRepository.findByUserId(Mockito.eq(3L))).thenReturn(userStatistics3);
  }

  @Test
  public void testGetUserRanking_general() {
      // when
      List<UserRanking> userRankingList = scoreBoardService.getUserRanking(null);

      // then
      Mockito.verify(userRepository, Mockito.times(1)).findAll();
      Mockito.verify(userStatisticsRepository, Mockito.times(28)).findByUserId(Mockito.any());

      assertEquals(3, userRankingList.size());

      assertEquals(1L, userRankingList.get(0).getUserId());
      assertEquals("user1", userRankingList.get(0).getUsername());
      assertEquals(1, userRankingList.get(0).getRank());
      assertEquals(170, userRankingList.get(0).getScore());
      assertEquals(2, userRankingList.get(0).getGameNum());

      assertEquals(3L, userRankingList.get(1).getUserId());
      assertEquals("user3", userRankingList.get(1).getUsername());
      assertEquals(2, userRankingList.get(1).getRank());
      assertEquals(160, userRankingList.get(1).getScore());
      assertEquals(2, userRankingList.get(1).getGameNum());

      assertEquals(2L, userRankingList.get(2).getUserId());
      assertEquals("user2", userRankingList.get(2).getUsername());
      assertEquals(3, userRankingList.get(2).getRank());
      assertEquals(90, userRankingList.get(2).getScore());
      assertEquals(2, userRankingList.get(2).getGameNum());  }

  @Test
  public void testGetUserRanking_specific() {
      // when
      List<UserRanking> userRankingList = scoreBoardService.getUserRanking(CityCategory.ASIA);

      // then
      Mockito.verify(userRepository, Mockito.times(1)).findAll();
      Mockito.verify(userStatisticsRepository, Mockito.times(56)).findByUserId(Mockito.any());

      assertEquals(3, userRankingList.size());
      assertEquals(3L, userRankingList.get(0).getUserId());
      assertEquals(120, userRankingList.get(0).getScore());
      assertEquals(1L, userRankingList.get(1).getUserId());
      assertEquals(100, userRankingList.get(1).getScore());
      assertEquals(2L, userRankingList.get(2).getUserId());
      assertEquals(60, userRankingList.get(2).getScore());
    }
}

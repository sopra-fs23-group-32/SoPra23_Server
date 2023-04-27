package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserStatisticsServiceTest {
  @Mock
  private UserStatisticsRepository userStatisticsRepository;
  @InjectMocks
  private UserStatisticsService userStatisticsService;
  private UserStatistics testUserStatistics;

  @BeforeEach
  public void setup() {
      MockitoAnnotations.openMocks(this);
      // given
      Long userId = 1L;
      testUserStatistics = new UserStatistics();
      testUserStatistics.setUserId(userId);
      testUserStatistics.initUserStatistics();

      // when -> any object is being found in the gameInfoRepository -> return the dummy
      Mockito.when(userStatisticsRepository.save(Mockito.any(UserStatistics.class))).thenReturn(testUserStatistics);
      Mockito.when(userStatisticsRepository.findByUserId(Mockito.eq(1L))).thenReturn(testUserStatistics);
      Mockito.when(userStatisticsRepository.findByUserId(Mockito.eq(999L))).thenReturn(null);
  }

  @Test
  public void testCreateUserService() {
      // when
      UserStatistics userStatistics = userStatisticsService.createUserService(1L);

      // then
      Mockito.verify(userStatisticsRepository, Mockito.times(1)).save(Mockito.any());
      Mockito.verify(userStatisticsRepository, Mockito.times(1)).flush();

      assertEquals(testUserStatistics.getUserId(), userStatistics.getUserId());
      assertEquals(testUserStatistics.getTotalScore(), userStatistics.getTotalScore());
      assertEquals(testUserStatistics.getTotalGameNum(), userStatistics.getTotalGameNum());
  }

  @Test
  public void testAddUserGameHistory_userStaExists() {
      // given
      Long userId = 1L;

      UserGameHistory gameHistory = new UserGameHistory();
      gameHistory.setUserId(userId);
      gameHistory.setGameScore(100);

      // when
      userStatisticsService.addUserGameHistory(userId, gameHistory);

      // then
      Mockito.verify(userStatisticsRepository, Mockito.times(2)).findByUserId(Mockito.any());

      assertNotNull(testUserStatistics.getGameHistoryList());
  }

  @Test
  public void testAddUserGameHistory_userStaNotExist() {
      // given
      Long userId = 999L;

      UserGameHistory gameHistory = new UserGameHistory();
      gameHistory.setUserId(userId);
      gameHistory.setGameScore(100);

      // verify a ResponseStatusException with status code NOT_FOUND is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> userStatisticsService.addUserGameHistory(userId, gameHistory)
      );
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void testUpdateUserStatistics() {
      // when
      userStatisticsService.updateUserStatistics(1L, 100, CityCategory.EUROPE);

      // then
      Mockito.verify(userStatisticsRepository, Mockito.times(2)).findByUserId(Mockito.any());

      assertEquals(100, testUserStatistics.getSpecificScore(CityCategory.EUROPE));
  }

  @Test
  public void testGetUserTotalScore() {
      // given
      testUserStatistics.updateScore(20, CityCategory.AFRICA);
      testUserStatistics.updateScore(50, CityCategory.ASIA);
      testUserStatistics.updateScore(90, CityCategory.OCEANIA);

      // when
      long totalScore = userStatisticsService.getUserTotalScore(1L);

      // then
      Mockito.verify(userStatisticsRepository, Mockito.times(2)).findByUserId(Mockito.any());

      assertEquals(160, totalScore);
  }

  @Test
  public void testGetUserSpecificScore() {
      // given
      testUserStatistics.updateScore(20, CityCategory.AFRICA);

      // when
      long specificScore = userStatisticsService.getUserSpecificScore(1L, CityCategory.AFRICA);

      // then
      Mockito.verify(userStatisticsRepository, Mockito.times(2)).findByUserId(Mockito.any());

      assertEquals(20, specificScore);
  }

  @Test
  public void testGetUserTotalGameNum() {
      // given
      testUserStatistics.updateGameNum(CityCategory.AFRICA);
      testUserStatistics.updateGameNum(CityCategory.NORTH_AMERICA);

      // when
      long totalGameNum = userStatisticsService.getUserTotalGameNum(1L);

      // then
      Mockito.verify(userStatisticsRepository, Mockito.times(2)).findByUserId(Mockito.any());

      assertEquals(2, totalGameNum);
  }

  @Test
    public void testGetUserSpecificGameNum() {
      // given
      testUserStatistics.updateGameNum(CityCategory.AFRICA);
      testUserStatistics.updateGameNum(CityCategory.AFRICA);
      testUserStatistics.updateGameNum(CityCategory.NORTH_AMERICA);

      // when
      long GameNumAfrica = userStatisticsService.getUserSpecificGameNum(1L, CityCategory.AFRICA);
      long GameNumNorthAmerica = userStatisticsService.getUserSpecificGameNum(1L, CityCategory.NORTH_AMERICA);

      // then
      Mockito.verify(userStatisticsRepository, Mockito.times(4)).findByUserId(Mockito.any());

      assertEquals(2, testUserStatistics.getSpecificGameNum(CityCategory.AFRICA));
      assertEquals(1, testUserStatistics.getSpecificGameNum(CityCategory.NORTH_AMERICA));
  }


}

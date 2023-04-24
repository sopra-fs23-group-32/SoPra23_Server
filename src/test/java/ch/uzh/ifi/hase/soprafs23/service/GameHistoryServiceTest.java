package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import ch.uzh.ifi.hase.soprafs23.entity.GameInfo;
import ch.uzh.ifi.hase.soprafs23.entity.UserGameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.UserStatistics;
import ch.uzh.ifi.hase.soprafs23.repository.GameInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class GameHistoryServiceTest {
//  @Mock
//  private GameInfoRepository gameInfoRepository;
//  @InjectMocks
//  private GameHistoryService gameHistoryService;
//  private UserGameHistory testGameHistory;
//  private UserStatistics testUserStatistics;
//  private GameInfo testGameInfo;
//
//  @BeforeEach
//  public void setup() {
//      MockitoAnnotations.openMocks(this);
//      // given
//      testGameHistory = new UserGameHistory(1L, 200);
//      testGameHistory.addAnswer("A");
//      testGameHistory.addAnswer("B");
//      testUserStatistics = new UserStatistics();
//      testUserStatistics.addGameHistory(testGameHistory);
//
//      testGameInfo = new GameInfo();
//      testGameInfo.setGameId(1L);
//      testGameInfo.setCategory(CityCategory.ASIA);
//      testGameInfo.setGameDate(new Date());
//      testGameInfo.setGameRounds(2);
//      testGameInfo.setPlayerNum(2);
//
//      // when -> any object is being found in the gameInfoRepository -> return the dummy
//      Mockito.when(gameInfoRepository.findByGameId(Mockito.eq(1L))).thenReturn(testGameInfo);
//      Mockito.when(gameInfoRepository.findByGameId(Mockito.eq(999L))).thenReturn(null);
//  }
//
//  @Test
//  public void getUserGameInfos_validInputs_success() {
//      // when
//      List<GameInfo> gameInfoList = gameHistoryService.getUserGameInfos(testUserStatistics);
//
//      // then
//      Mockito.verify(gameInfoRepository, Mockito.times(1)).findByGameId(Mockito.any());
//
//      assertEquals(testGameInfo.getGameId(), gameInfoList.get(0).getGameId());
//      assertEquals(testGameInfo.getCategory(), gameInfoList.get(0).getCategory());
//      assertNotNull(gameInfoList.get(0).getGameDate());
//      assertEquals(testGameInfo.getGameRounds(), gameInfoList.get(0).getGameRounds());
//      assertEquals(testGameInfo.getPlayerNum(), gameInfoList.get(0).getPlayerNum());
//  }
//
//  @Test
//  public void searchGameInfoById_validInputs_success() {
//      Long gameId = 1L;
//
//      // when
//      GameInfo gameInfo = gameHistoryService.searchGameInfoById(gameId);
//
//      // then
//      Mockito.verify(gameInfoRepository, Mockito.times(2)).findByGameId(gameId);
//
//      assertEquals(testGameInfo.getGameId(), gameInfo.getGameId());
//      assertEquals(testGameInfo.getCategory(), gameInfo.getCategory());
//      assertNotNull(gameInfo.getGameDate());
//      assertEquals(testGameInfo.getGameRounds(), gameInfo.getGameRounds());
//      assertEquals(testGameInfo.getPlayerNum(), gameInfo.getPlayerNum());
//    }
//
//    @Test
//    public void searchGameInfoById_invalidInputs_throwsException() {
//        Long gameId = 999L;
//
//        // verify a ResponseStatusException with status code NOT_FOUND is thrown
//        ResponseStatusException exception = assertThrows(
//                ResponseStatusException.class,
//                () -> gameHistoryService.searchGameInfoById(gameId)
//        );
//        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
//    }
//
//    @Test
//    public void searchGameHistoryById_validInputs_success() {
//      Long gameId = 1L;
//
//      // when
//      UserGameHistory gameHistory = gameHistoryService.searchGameHistoryById(testUserStatistics, gameId);
//
//      assertEquals(testGameHistory.getGameId(), gameHistory.getGameId());
//      assertEquals(testGameHistory.getGameScore(), gameHistory.getGameScore());
//      Iterator<String> expectedIterator = testGameHistory.getAnswerList();
//      Iterator<String> actualIterator = gameHistory.getAnswerList();
//      while (expectedIterator.hasNext() && actualIterator.hasNext()) {
//          String expectedValue = expectedIterator.next();
//          String actualValue = actualIterator.next();
//          assertEquals(expectedValue, actualValue);
//      }
//      assertFalse(expectedIterator.hasNext());
//      assertFalse(actualIterator.hasNext());
//    }
//
//    @Test
//    public void searchGameHistoryById_invalidGameId_throwsException() {
//      Long gameId = 999L;
//
//      // verify a ResponseStatusException with status code NOT_FOUND is thrown
//      ResponseStatusException exception = assertThrows(
//              ResponseStatusException.class,
//              () -> gameHistoryService.searchGameHistoryById(testUserStatistics, gameId)
//      );
//      assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
//    }
//
//    @Test
//    public void searchGameHistoryById_invalidGameId_userStatistics_throwsException() {
//      Long gameId = 1L;
//
//      // given
//      testGameHistory.setGameId(2L);
//
//      // verify a ResponseStatusException with status code NOT_FOUND is thrown
//      ResponseStatusException exception = assertThrows(
//              ResponseStatusException.class,
//              () -> gameHistoryService.searchGameHistoryById(testUserStatistics, gameId)
//      );
//      assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
//    }
}

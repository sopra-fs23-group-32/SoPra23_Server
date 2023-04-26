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
  @Mock
  private GameInfoRepository gameInfoRepository;
  @InjectMocks
  private GameHistoryService gameHistoryService;
  private UserGameHistory testGameHistory;
  private UserStatistics testUserStatistics;
  private GameInfo testGameInfo;

  @BeforeEach
  public void setup() {
      MockitoAnnotations.openMocks(this);
      // given
      Long gameId = 1L;

      testGameHistory = new UserGameHistory();
      testGameHistory.setGameId(gameId);
      testGameHistory.setGameScore(200);
      testGameHistory.addAnswer("Zurich");
      testGameHistory.addAnswer("Basel");
      testUserStatistics = new UserStatistics();
      testUserStatistics.addGameHistory(testGameHistory);

      testGameInfo = new GameInfo();
      testGameInfo.setGameId(gameId);
      testGameInfo.setCategory(CityCategory.EUROPE);
      testGameInfo.setGameDate(new Date());
      testGameInfo.setGameRounds(2);
      testGameInfo.setPlayerNum(2);

      // when -> any object is being found in the gameInfoRepository -> return the dummy
      Mockito.when(gameInfoRepository.save(Mockito.any(GameInfo.class))).thenReturn(testGameInfo);
      Mockito.when(gameInfoRepository.findByGameId(Mockito.eq(1L))).thenReturn(testGameInfo);
      Mockito.when(gameInfoRepository.findByGameId(Mockito.eq(999L))).thenReturn(null);
  }

  @Test
  public void testCreateGameInfo() {
      // when
      GameInfo gameInfo = new GameInfo();
      gameInfo.setGameId(1L);
      gameInfo.setCategory(CityCategory.EUROPE);
      gameInfo.setGameRounds(2);
      gameInfo.setPlayerNum(2);
      GameInfo newGameInfo = gameHistoryService.createGameInfo(gameInfo);

      // then
      Mockito.verify(gameInfoRepository, Mockito.times(1)).save(Mockito.any());
      Mockito.verify(gameInfoRepository, Mockito.times(1)).flush();

      assertEquals(testGameInfo.getGameId(), newGameInfo.getGameId());
      assertEquals(testGameInfo.getCategory(), newGameInfo.getCategory());
      assertNotNull(newGameInfo.getGameDate());
      assertEquals(testGameInfo.getGameRounds(), newGameInfo.getGameRounds());
      assertEquals(testGameInfo.getPlayerNum(), newGameInfo.getPlayerNum());
  }

  @Test
  public void testSearchGameInfoById_validInput() {
      Long gameId = 1L;

      // when
      GameInfo gameInfo = gameHistoryService.searchGameInfoById(gameId);

      // then
      Mockito.verify(gameInfoRepository, Mockito.times(2)).findByGameId(gameId);

      assertEquals(testGameInfo.getGameId(), gameInfo.getGameId());
      assertEquals(testGameInfo.getCategory(), gameInfo.getCategory());
      assertNotNull(gameInfo.getGameDate());
      assertEquals(testGameInfo.getGameRounds(), gameInfo.getGameRounds());
      assertEquals(testGameInfo.getPlayerNum(), gameInfo.getPlayerNum());
    }

    @Test
    public void testSearchGameInfoById_invalidInput() {
        Long gameId = 999L;

        // verify a ResponseStatusException with status code NOT_FOUND is thrown
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> gameHistoryService.searchGameInfoById(gameId)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}

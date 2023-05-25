package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
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

public class GameServiceTest {
  @Mock
  private GameRepository gameRepository;
  @InjectMocks
  private GameService gameService;
  private Game testGame;

  @BeforeEach
  public void setup() {
      MockitoAnnotations.openMocks(this);
      // given
      Long gameId = 1L;

      testGame = new Game();
      testGame.initGame();
      testGame.setGameId(gameId);
      testGame.setCategory(CityCategory.EUROPE);
      testGame.setCountdownTime(10);
      testGame.setTotalRounds(2);
      testGame.setQuestions(0, "Zurich");
      testGame.setQuestions(1, "Geneva");
      testGame.setQuestions(2, "Basel");
      testGame.setQuestions(3, "Bern");
      testGame.updateCurrentAnswer("Bern");



      // when -> any object is being found in the gameInfoRepository -> return the dummy
      Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenReturn(testGame);
      Mockito.when(gameRepository.findByGameId(Mockito.eq(1L))).thenReturn(testGame);
      Mockito.when(gameRepository.findByGameId(Mockito.eq(999L))).thenReturn(null);
  }

  @Test
  public void testCreateGame() {
      // given
      Game game = new Game();
      game.setGameId(1L);
      game.setCategory(CityCategory.EUROPE);
      game.setCountdownTime(10);
      game.setTotalRounds(2);

      // when
      Game newGame = gameService.createGame(game);

      // then
      Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());
      Mockito.verify(gameRepository, Mockito.times(1)).flush();

      assertEquals(testGame.getGameId(), newGame.getGameId());
      assertEquals(testGame.getCategory(), newGame.getCategory());
      assertEquals(testGame.getCountdownTime(), newGame.getCountdownTime());
      assertEquals(testGame.getTotalRounds(), newGame.getTotalRounds());
  }

  @Test
  public void testGetAllGames_withoutPlayer() {
      // given
//      testGame.addPlayer(new Player());

//      List<Game> games = new ArrayList<>();
//      games.add(testGame);

      // when
      List<Game> gameList = gameService.getAllGames();
//      Mockito.when(gameRepository.findAll()).thenReturn(games);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findAll();

      assertEquals(0, gameList.size());
//      assertEquals(testGame.getGameId(), gameList.get(0));
    }

  @Test
  public void testAddPlayer_gameIdExists() {
      // given
      User user = new User();
      user.setUserId(1L);
      user.setUsername("username");

      // when
      gameService.addPlayer(testGame.getGameId(), user);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(1, testGame.getPlayerNum());
      assertEquals(user.getUserId(), testGame.getPlayerList().next().getUserId());
      assertEquals(user.getUsername(), testGame.getPlayerList().next().getPlayerName());
  }

  @Test
  public void testAddPlayer_gameIdNotExist() {

      // verify a ResponseStatusException with status code NOT_FOUND is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> gameService.addPlayer(999L, new User())
      );
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void testGetAllPlayers() {
      // given
      User user1 = new User();
      user1.setUserId(1L);
      user1.setUsername("username1");
      User user2 = new User();
      user2.setUserId(2L);
      user2.setUsername("username2");
      gameService.addPlayer(testGame.getGameId(), user1);
      gameService.addPlayer(testGame.getGameId(), user2);

      // when
      List<Long> userIdList = gameService.getAllPlayers(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(5)).findByGameId(Mockito.any());

      assertEquals(2, userIdList.size());
      assertEquals(user1.getUserId(), userIdList.get(0));
      assertEquals(user2.getUserId(), userIdList.get(1));
  }

  @Test
  public void testGoNextRound_gameNotEnded() {
      // when
      Question question = gameService.goNextRound(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertNotNull(question.getOption1());
      assertNotNull(question.getOption2());
      assertNotNull(question.getOption3());
      assertNotNull(question.getOption4());
      assertNotNull(question.getCorrectOption());
      assertNotNull(question.getPictureUrl());
      assertEquals(GameStatus.ANSWERING, testGame.getGameStatus());
  }

  @Test
  public void testGoNextRound_gameEnded() {
      testGame.addCurrentRound();
      testGame.addCurrentRound();

      // verify a ResponseStatusException with status code BAD_REQUEST is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> gameService.goNextRound(1L)
      );

      assertEquals(HttpStatus.CONFLICT, exception.getStatus());

  }

  @Test
  public void testGetQuestions() {
      // when
      Question question = gameService.getQuestions(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(testGame.getQuestions(0), question.getOption1());
      assertEquals(testGame.getQuestions(1), question.getOption2());
      assertEquals(testGame.getQuestions(2), question.getOption3());
      assertEquals(testGame.getQuestions(3), question.getOption4());
      assertEquals(testGame.getCurrentAnswer(), question.getCorrectOption());
  }

  @Test
  public void testSubmitAnswer_correctAnswer() {
      // given
      Long gameId = 1L;
      Long userId = 1L;

      Player player = new Player();
      player.setUserId(userId);
      testGame.addPlayer(player);

      Answer answer = new Answer();
      answer.setAnswer("Zurich");
      answer.setTimeTaken(3);
      testGame.updateCurrentAnswer("Zurich");

      // when
      int score = gameService.submitAnswer(gameId, userId, answer);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(48, score);
      assertEquals(answer.getAnswer(), gameService.searchPlayerById(testGame, userId).getAnswerList().next());
  }


  @Test
  public void testSubmitAnswer_wrongAnswer() {
      // given
      Long gameId = 1L;
      Long userId = 1L;

      Player player = new Player();
      player.setUserId(userId);
      testGame.addPlayer(player);

      Answer answer = new Answer();
      answer.setAnswer("Zurich");
      answer.setTimeTaken(3);
      testGame.updateCurrentAnswer("Basel");

      // when
      int score = gameService.submitAnswer(gameId, userId, answer);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(0, score);
  }

  @Test
  public void testIfAllAnswered_true() {
      Player player1 = new Player();
      player1.setHasAnswered(true);
      Player player2 = new Player();
      player2.setHasAnswered(true);
      testGame.addPlayer(player1);
      testGame.addPlayer(player2);

      // when
      boolean allAnswered = gameService.checkIfAllAnswered(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertTrue(allAnswered);
      assertEquals(GameStatus.WAITING, testGame.getGameStatus());
  }

  @Test
  public void testIfAllAnswered_false() {
      Player player1 = new Player();
      player1.setHasAnswered(true);
      Player player2 = new Player();
      player2.setHasAnswered(false);
      testGame.addPlayer(player1);
      testGame.addPlayer(player2);

      // when
      boolean allAnswered = gameService.checkIfAllAnswered(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertFalse(allAnswered);
    }

  @Test
  public void testGetRanking() {
      // given
      Player player1 = new Player();
      player1.setPlayerName("player1");
      player1.addScore(50);
      testGame.addPlayer(player1);
      Player player2 = new Player();
      player2.setPlayerName("player2");
      player2.addScore(70);
      testGame.addPlayer(player2);
      Player player3 = new Player();
      player3.setPlayerName("player3");
      player3.addScore(70);
      testGame.addPlayer(player3);

      // when
      List<PlayerRanking> playerRankingList = gameService.getRanking(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(1, playerRankingList.get(0).getRank());
      assertEquals(player2.getPlayerName(), playerRankingList.get(0).getPlayerName());
      assertEquals(player2.getScore(), playerRankingList.get(0).getScore());
      assertEquals(1, playerRankingList.get(1).getRank());
      assertEquals(player3.getPlayerName(), playerRankingList.get(1).getPlayerName());
      assertEquals(player3.getScore(), playerRankingList.get(1).getScore());
      assertEquals(2, playerRankingList.get(2).getRank());
      assertEquals(player1.getPlayerName(), playerRankingList.get(2).getPlayerName());
      assertEquals(player1.getScore(), playerRankingList.get(2).getScore());
  }

  @Test
  public void testGetGameResult_gameEnded() {
      // given
      Player player1 = new Player();
      player1.setPlayerName("player1");
      player1.addScore(50);
      testGame.addPlayer(player1);
      Player player2 = new Player();
      player2.setPlayerName("player2");
      player2.addScore(70);
      testGame.addPlayer(player2);
      Player player3 = new Player();
      player3.setPlayerName("player3");
      player3.addScore(70);
      testGame.addPlayer(player3);
      testGame.addCurrentRound();
      testGame.addCurrentRound();

      // when
      List<String> winnerList = gameService.getGameResult(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(player2.getPlayerName(), winnerList.get(0));
      assertEquals(player3.getPlayerName(), winnerList.get(1));
  }

  @Test
  public void testGetGameResult_gameNotEnded() {

      // verify a ResponseStatusException with status code BAD_REQUEST is thrown
      ResponseStatusException exception = assertThrows(
              ResponseStatusException.class,
              () -> gameService.getGameResult(1L)
      );

      assertEquals(HttpStatus.CONFLICT, exception.getStatus());

  }

  @Test
  public void testCloseGame_gameEnded() {
      // given
      testGame.addCurrentRound();
      testGame.addCurrentRound();

      // when
      gameService.closeGame(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(GameStatus.DELETED, testGame.getGameStatus());
  }   




  @Test
  public void testLeaveGame() {
      // given
      Long userId = 1L;
      Player player = new Player();
      player.setUserId(userId);
      testGame.addPlayer(player);

      // when
      gameService.leaveGame(1L, userId, 1);

      // then
      Mockito.verify(gameRepository, Mockito.times(4)).findByGameId(Mockito.any());

      assertEquals(0, testGame.getPlayerNum());
  }

  @Test
  public void testGetGameInfo() {
      // given
      testGame.addPlayer(new Player());
      testGame.addPlayer(new Player());
      testGame.addCurrentRound();
      testGame.addCurrentRound();

      // when
      GameInfo gameInfo = gameService.getGameInfo(1L);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      List<String> labelList = new ArrayList<>();
      Iterator<String> labelIterator = gameInfo.getLabelList();
      while (labelIterator.hasNext()) {
          labelList.add(labelIterator.next());
      }
      assertEquals(testGame.getGameId(), gameInfo.getGameId());
      assertEquals(testGame.getCategory(), gameInfo.getCategory());
      assertEquals(testGame.getTotalRounds(), gameInfo.getGameRounds());
      assertEquals(testGame.getPlayerNum(), gameInfo.getPlayerNum());
  }

  @Test
    public void testGetGameHistory() {
      // given
      Long userId = 1L;

      Player player = new Player();
      player.setUserId(userId);
      player.addScore(30);
      player.addAnswer("Zurich");
      testGame.addPlayer(player);
      testGame.addCurrentRound();
      testGame.addCurrentRound();

      // when
      UserGameHistory gameHistory = gameService.getUserGameHistory(1L, userId);

      // then
      Mockito.verify(gameRepository, Mockito.times(2)).findByGameId(Mockito.any());

      assertEquals(testGame.getGameId(), gameHistory.getGameId());
      assertEquals(player.getScore(), gameHistory.getGameScore());
      assertEquals(player.getAnswerList().next(), gameHistory.getAnswerList().next());
  }
}

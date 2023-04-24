package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setUsername("firstname lastname");
        user.setPassword("12345678");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreateDay(new Date());
        user.setBirthDay(new Date());
        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        // check content
        assertEquals(user.getUserId(), userGetDTO.getUserId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getPassword(), userGetDTO.getPassword());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getCreateDay(), userGetDTO.getCreateDay());
        assertEquals(user.getBirthDay(), userGetDTO.getBirthDay());
    }

    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");
        userPostDTO.setPassword("password");
        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        // check content
        assertEquals(userPostDTO.getUsername(), user.getUsername());
        assertEquals(userPostDTO.getPassword(), user.getPassword());
    }

    @Test
    public void testCreateUser_fromUserPutDTO_toUser_success() {
        // create UserPostDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("username");
        userPutDTO.setPassword("password");
        userPutDTO.setBirthDay(new Date());
        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        // check content
        assertEquals(userPutDTO.getUsername(), user.getUsername());
        assertEquals(userPutDTO.getPassword(), user.getPassword());
        assertEquals(userPutDTO.getBirthDay(), user.getBirthDay());
    }

    @Test
    public void testGetGame_fromGame_toGameGetDTO_success() {
        // create Game
        Game game = new Game();
        game.setGameId(1L);
        // MAP -> Create GameGetDTO
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        // check content
        assertEquals(game.getGameId(), gameGetDTO.getGameId());
    }

    @Test
    public void testCreateGame_fromGamePostDTO_toGame_success() {
        // create GamePostDTO
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setCategory(CityCategory.ASIA);
        gamePostDTO.setCountdownTime(20);
        gamePostDTO.setTotalRounds(5);
        // MAP -> Game
        Game game = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);
        // check content
        assertEquals(gamePostDTO.getCategory(), game.getCategory());
        assertEquals(gamePostDTO.getCountdownTime(), game.getCountdownTime());
        assertEquals(gamePostDTO.getTotalRounds(), game.getTotalRounds());
    }

    @Test
    public void testGetQuestion_fromQuestion_toQuestionGetDTO_success() {
        // create Question
        Question question = new Question("A", "B", "C", "D", "A", "url");
        // MAP -> QuestionGetDTO
        QuestionGetDTO questionGetDTO = DTOMapper.INSTANCE.convertEntityToQuestionGetDTO(question);
        // check content
        assertEquals(question.getOption1(), questionGetDTO.getOption1());
        assertEquals(question.getOption2(), questionGetDTO.getOption2());
        assertEquals(question.getOption3(), questionGetDTO.getOption3());
        assertEquals(question.getOption4(), questionGetDTO.getOption4());
        assertEquals(question.getCorrectOption(), questionGetDTO.getCorrectOption());
        assertEquals(question.getPictureUrl(), questionGetDTO.getPictureUrl());
    }

    @Test
    public void testCreateAnswer_fromAnswerPostDTO_toAnswer_success() {
        // create AnswerPostDTO
        AnswerPostDTO answerPostDTO = new AnswerPostDTO();
        answerPostDTO.setAnswer("C");
        answerPostDTO.setTimeTaken(3);
        // MAP -> Answer
        Answer answer = DTOMapper.INSTANCE.convertAnswerPostDTOtoEntity(answerPostDTO);
        // check content
        assertEquals(answerPostDTO.getAnswer(), answer.getAnswer());
        assertEquals(answerPostDTO.getTimeTaken(), answer.getTimeTaken());
    }

    @Test
    public void testGetGameInfo_fromGameInfo_toGameInfoGetDTO_success() {
        // create GameInfo
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameId(1L);
        gameInfo.setCategory(CityCategory.ASIA);
        gameInfo.setGameDate(new Date());
        gameInfo.setGameRounds(2);
        gameInfo.setPlayerNum(4);
        // MAP -> Create gameInfoGetDTO
        GameInfoGetDTO gameInfoGetDTO = DTOMapper.INSTANCE.convertEntityToGameInfoGetDTO(gameInfo);
        // check content
        assertEquals(gameInfo.getGameId(), gameInfoGetDTO.getGameId());
        assertEquals(gameInfo.getCategory(), gameInfoGetDTO.getCategory());
        assertEquals(gameInfo.getGameDate(), gameInfoGetDTO.getGameDate());
        assertEquals(gameInfo.getGameRounds(), gameInfoGetDTO.getGameRounds());
        assertEquals(gameInfo.getPlayerNum(), gameInfoGetDTO.getPlayerNum());
    }

    @Test
    public void testGetPlayerRanking_fromPlayerRanking_toPlayerRankingGetDTO_success() {
        // create PlayerRanking
        PlayerRanking playerRanking = new PlayerRanking("testPlayer", 20, 2);
        // MAP -> Create PlayerRankingGetDTO
        PlayerRankingGetDTO playerRankingGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerRankingGetDTO(playerRanking);
        // check content
        assertEquals(playerRanking.getPlayerName(), playerRankingGetDTO.getPlayerName());
        assertEquals(playerRanking.getScore(), playerRankingGetDTO.getScore());
        assertEquals(playerRanking.getRank(), playerRankingGetDTO.getRank());
    }

    @Test
    public void testGetGameResult_fromGameResult_toGameResultGetDTO_success() {
        // create GameResult
        Player player = new Player();
        player.setUserId(1L);
        player.setPlayerName("TestPlayer");
        List<String> winnerList = new ArrayList<>(List.of(player.getPlayerName()));
        GameResult gameResult = new GameResult(winnerList);
        // MAP -> Create GameResultGetDTO
        GameResultGetDTO gameResultGetDTO = DTOMapper.INSTANCE.convertEntityToGameResultGetDTO(gameResult);
        // check content
        assertEquals(gameResult.getWinnerList(), gameResultGetDTO.getWinnerList());
    }
}

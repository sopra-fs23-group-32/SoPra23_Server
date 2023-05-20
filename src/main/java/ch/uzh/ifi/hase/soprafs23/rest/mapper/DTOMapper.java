package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map
 * the internal representation of an entity (e.g., the User) to the external/API representation
 * (e.g., UserGetDTO for getting, UserPostDTO for creating) and vice versa.
 * Always created one mapper for (GET) and one mapper for (POST).
 */
@Mapper
public interface DTOMapper {
    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "birthDay", target = "birthDay")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createDay", target = "createDay")
    @Mapping(source = "birthDay", target = "birthDay")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "createDay", target = "createDay")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "gameNum", target = "gameNum")
    @Mapping(source = "rank", target = "rank")
    UserRankingGetDTO convertEntityToUserRankingGetDTO(UserRanking userRanking);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "currentRound", target = "currentRound")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "countdownTime", target = "countdownTime")
    @Mapping(source = "currentAnswer", target = "currentAnswer")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "countdownTime", target = "countdownTime")
    Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

    @Mapping(source = "option1", target = "option1")
    @Mapping(source = "option2", target = "option2")
    @Mapping(source = "option3", target = "option3")
    @Mapping(source = "option4", target = "option4")
    @Mapping(source = "correctOption", target = "correctOption")
    @Mapping(source = "pictureUrl", target = "pictureUrl")
    QuestionGetDTO convertEntityToQuestionGetDTO(Question question);

    @Mapping(source = "answer", target = "answer")
    @Mapping(source = "timeTaken", target = "timeTaken")
    Answer convertAnswerPostDTOtoEntity(AnswerPostDTO answerPostDTO);

    @Mapping(source = "playerName", target = "playerName")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "rank", target = "rank")
    PlayerRankingGetDTO convertEntityToPlayerRankingGetDTO(PlayerRanking playerRanking);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "gameDate", target = "gameDate")
    @Mapping(source = "gameRounds", target = "gameRounds")
    @Mapping(source = "playerNum", target = "playerNum")
    GameInfoGetDTO convertEntityToGameInfoGetDTO(GameInfo gameInfo);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "gameScore", target = "gameScore")
   
    GameHistoryGetDTO convertEntityToGameHistoryGetDTO(UserGameHistory userGameHistory);

    @Mapping(source = "answer", target = "answer")
    @Mapping(source = "correctAnswer", target = "correctAnswer")
    GameHistoryAnswerGetDTO convertEntityToGameHistoryAnswerGetDTO(GameHistoryAnswer gameHistoryAnswer);
}

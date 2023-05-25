package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */
@Entity
@IdClass(PlayerId.class)
public class Player {

    @Id
    private Long userId;
    @Id
    private Long gameId;

    private String playerName;
    private int score = 0;
    private int correctCount = 0;
    @ElementCollection
    private final List<String> answerList = new ArrayList<>();

    private boolean hasAnswered = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_gameId")
    private Game game;
    public void setGame(Game game) {this.game = game;}
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "player_game",
//        joinColumns = { @JoinColumn(name = "player_userId", referencedColumnName = "userId"),
//            @JoinColumn(name = "player_gameId", referencedColumnName = "gameId")},
//        inverseJoinColumns = @JoinColumn(name = "game_gameId")
//    )
//    private List<Game> gameList = new ArrayList<>();
//    public void addGame(Game game) {gameList.add(game);}

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public Long getGameId() {return gameId;}
    public void setGameId(Long gameId) {this.gameId = gameId;}

    public String getPlayerName() {return playerName;}
    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public int getScore() {return score;}
    public void addScore(int score) {this.score += score;}

    public void addCorrectCount() {this.correctCount += 1;}
    public float getCorrectRate() {return (float)correctCount / answerList.size();}

    public void addAnswer(String newAnswer) {answerList.add(newAnswer);}
    public Iterator<String> getAnswerList() {return answerList.iterator();}

    public boolean getHasAnswered(){return hasAnswered;}
    public void setHasAnswered(boolean hasAnswered){this.hasAnswered = hasAnswered;}
}

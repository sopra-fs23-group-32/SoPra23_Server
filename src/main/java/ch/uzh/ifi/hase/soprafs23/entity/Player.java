package ch.uzh.ifi.hase.soprafs23.entity;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zilong Deng
 */
@Entity
@Table(name = "PLAYER")
public class Player {

    @Id
    private Long userId;

    private String playerName;

    private int score = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_gameId")
    private Game game;

//    private final List<String> answerList = new ArrayList<>();

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getPlayerName() {return playerName;}
    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public int getScore() {return score;}
    public void addScore(int score) {this.score += score;}

    public void setGame(Game game) {this.game = game;}

    //    public void addAnswer(String newAnswer) {answerList.add(newAnswer);}
//    public Iterator<String> getAnswerList() {return answerList.iterator();}
}

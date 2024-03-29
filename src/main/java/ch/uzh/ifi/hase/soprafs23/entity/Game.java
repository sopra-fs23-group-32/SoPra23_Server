package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;
import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WebSocketType;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * @author Zilong Deng
 */
@Entity
@Table(name = "GAME")
public class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue
    private Long gameId;
    @Column(nullable = false)
    private CityCategory category;
    @Column(nullable = false)
    private int totalRounds;
    @Column(nullable = false)
    private int countdownTime;

    GameStatus gameStatus;
    private int currentRound;
    private String currentAnswer="";
    private int playerNum;
    private int playerNumForSur;
    private String Q1="", Q2="", Q3="", Q4="";
    private String ImgUrl = "";
    private String hostname = "none";

    @ElementCollection
    private List<String> labelList = new ArrayList<>();

    // orphanRemoval = true
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Player> playerList = new ArrayList<>();

    public void initGame() {
        currentRound = 0;
        currentAnswer = "";
        Q1 = "";Q2 = "";Q3 = "";Q4 = "";
        ImgUrl = "";
        hostname = "none";
        playerNum = 0;
        playerNumForSur = 0;
        gameStatus = GameStatus.SETUP;
    }

    public Long getGameId() {return gameId;}
    public void setGameId(Long gameId) {this.gameId = gameId;}
    
    public CityCategory getCategory() {return category;}
    public void setCategory(CityCategory category) {this.category = category;}

    public int getTotalRounds() {return totalRounds;}
    public void setTotalRounds(int totalRounds) {this.totalRounds = totalRounds;}

    public int getCountdownTime() {return countdownTime;}
    public void setCountdownTime(int countdownTime) {this.countdownTime = countdownTime;}

    public int getCurrentRound() {return currentRound;}
    public void addCurrentRound() {currentRound += 1;}
    public boolean isGameEnded() {return currentRound >= totalRounds;}

    public String getCurrentAnswer() {return currentAnswer;}
    public void updateCurrentAnswer(String currentAnswer) {
        this.currentAnswer = currentAnswer;
        labelList.add(currentAnswer);
    }
    public Iterator<String> getLabelList() {return labelList.iterator();}

    public void setQuestions(int i, String string) {
        switch (i) {
            case 0 -> Q1 = string;
            case 1 -> Q2 = string;
            case 2 -> Q3 = string;
            default -> Q4 = string;
        }
    }
    public String getQuestions(int i) {
        return switch (i) {
            case 0 -> Q1;
            case 1 -> Q2;
            case 2 -> Q3;
            default -> Q4;
        };
    }

    public String getImgUrl() {return ImgUrl;}
    public void setImgUrl(String imgUrl) {ImgUrl = imgUrl;}

    public void addPlayer(Player newPlayer) {
        playerList.add(newPlayer);
        if (hostname.equals("none")) {
            hostname = newPlayer.getPlayerName();
        }
        playerNum += 1;
    }

    public void deletePlayer(Long playerId) {
        playerList.removeIf(player -> Objects.equals(player.getUserId(), playerId));
        playerNum -= 1;
    }
    public Iterator<Player> getPlayerList() { return playerList.iterator();}
    public int getPlayerNum() {return playerNum;}

    public void setPlayerNumForSur() {playerNumForSur = playerNum;}
    public int getPlayerNumForSur() {return playerNumForSur;}

    public GameStatus getGameStatus(){return gameStatus;}
    public void setGameStatus(GameStatus gameStatus){this.gameStatus = gameStatus;}

    public void setHostname(String hostname) {this.hostname = hostname;}
    public String getHostname() {return hostname;}

    public List<PlayerRanking> getRanking() {
        // Sort player scores in descending order
        List<Player> sortedPlayerList = playerList.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed()).toList();

        List<PlayerRanking> playerRankingList = new ArrayList<>();
        int currentRank = 0;
        int currentScore = Integer.MAX_VALUE;
        for (Player player: sortedPlayerList) {
            int playerScore = player.getScore();
            if (playerScore < currentScore) {
                currentRank ++;
                currentScore = playerScore;
            }
            playerRankingList.add(new PlayerRanking(
                    player.getPlayerName(), player.getScore(), currentRank
            ));
        }
        return playerRankingList;
    }

    public List<String> getWinners() {
        List<PlayerRanking> playerRankingList = getRanking();

        List<String> winnerList = new ArrayList<>();
        for (PlayerRanking playerRanking : playerRankingList) {
            if (playerRanking.getRank() == 1) {
                winnerList.add(playerRanking.getPlayerName());
            }
        }
        return winnerList;
    }

//    public void setExecuting(boolean executing) {isExecuting = executing;}
//    public boolean isExecuting() {return isExecuting;}
}

package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
    @Column()
    private int currentRound;
    @Column(nullable = false)
    private int countdownTime;
    @Column()
    private String currentAnswer;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> playerList = new ArrayList<>();

    @ElementCollection
    private List<String> labelList = new ArrayList<>();

    public void initGame() {
        currentRound = 0;
        currentAnswer = null;
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
    public void addCurrentRound() {currentRound ++;}
    public boolean isGameEnded() {return currentRound >= totalRounds;}

    public String getCurrentAnswer() {return currentAnswer;}
    public void updateCurrentAnswer(String currentAnswer) {
        this.currentAnswer = currentAnswer;
        labelList.add(currentAnswer);
    }

    public Iterator<String> getLabelList() {return labelList.iterator();}

    public void addPlayer(Player newPlayer) {
        playerList.add(newPlayer);
        System.out.println("Player added: " + newPlayer.getPlayerName());
    }
    public Iterator<Player> getPlayerList() { return playerList.iterator();}
    public int getPlayerNum() {return playerList.size();}

    public void deletePlayer(Long playerId) {
        playerList.removeIf(player -> Objects.equals(player.getUserId(), playerId));
    }

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
}

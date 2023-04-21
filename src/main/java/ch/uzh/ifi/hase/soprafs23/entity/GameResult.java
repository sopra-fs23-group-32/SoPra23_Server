package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.List;

public class GameResult {
    private List<Player> winnerList;
    private List<PlayerRanking> playerRankingList;

    public GameResult(List<Player> winnerList, List<PlayerRanking> playerRankingList) {
        this.winnerList = winnerList;
        this.playerRankingList = playerRankingList;
    }

    public List<Player> getWinnerList() {return winnerList;}
    public void setWinnerList(List<Player> winnerList) {this.winnerList = winnerList;}

    public List<PlayerRanking> getPlayerRankingList() {return playerRankingList;}
    public void setPlayerRankingList(List<PlayerRanking> playerRankingList) {this.playerRankingList = playerRankingList;}
}

package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.PlayerRanking;

import java.util.List;

public class GameResultGetDTO {
    private List<Player> winnerList;
    private List<PlayerRanking> playerRankingList;

    public List<Player> getWinnerList() {return winnerList;}
    public void setWinnerList(List<Player> winnerList) {this.winnerList = winnerList;}

    public List<PlayerRanking> getPlayerRankingList() {return playerRankingList;}
    public void setPlayerRankingList(List<PlayerRanking> playerRankingList) {this.playerRankingList = playerRankingList;}
}

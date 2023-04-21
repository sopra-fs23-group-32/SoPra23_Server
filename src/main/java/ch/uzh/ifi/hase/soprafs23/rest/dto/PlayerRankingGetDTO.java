package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

public class PlayerRankingGetDTO {
    private Player player;
    private int rank;

    public Player getPlayer() {return player;}
    public void setPlayer(Player player) {this.player = player;}

    public int getRank() {return rank;}
    public void setRank(int rank) {this.rank = rank;}
}

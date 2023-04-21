package ch.uzh.ifi.hase.soprafs23.entity;

public class PlayerRanking {
    private Player player;
    private int rank;

    public PlayerRanking(Player player, int rank) {
        this.player = player;
        this.rank = rank;
    }

    public Player getPlayer() {return player;}
    public void setPlayer(Player player) {this.player = player;}

    public int getRank() {return rank;}
    public void setRank(int rank) {this.rank = rank;}
}

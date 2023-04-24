package ch.uzh.ifi.hase.soprafs23.entity;

public class PlayerRanking {
    private String playerName;
    private int rank;

    public PlayerRanking(String playerName, int rank) {
        this.playerName = playerName;
        this.rank = rank;
    }

    public String getPlayerName() {return playerName;}
    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public int getRank() {return rank;}
    public void setRank(int rank) {this.rank = rank;}
}

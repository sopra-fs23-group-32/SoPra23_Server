package ch.uzh.ifi.hase.soprafs23.entity;

public class PlayerRanking {
    private String playerName;
    private int score;
    private int rank;

    public PlayerRanking(String playerName, int score, int rank) {
        this.playerName = playerName;
        this.score = score;
        this.rank = rank;
    }

    public String getPlayerName() {return playerName;}
    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public int getScore() {return score;}
    public void setScore(int score) {this.score = score;}

    public int getRank() {return rank;}
    public void setRank(int rank) {this.rank = rank;}
}

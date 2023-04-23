package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.List;

public class GameResult {
    private List<String> winnerList;

    public GameResult(List<String> winnerList) {
        this.winnerList = winnerList;
    }

    public List<String> getWinnerList() {return winnerList;}
    public void setWinnerList(List<String> winnerList) {this.winnerList = winnerList;}

}

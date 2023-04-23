package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.PlayerRanking;

import java.util.List;

public class GameResultGetDTO {
    private List<String> winnerList;

    public List<String> getWinnerList() {return winnerList;}
    public void setWinnerList(List<String> winnerList) {this.winnerList = winnerList;}
}

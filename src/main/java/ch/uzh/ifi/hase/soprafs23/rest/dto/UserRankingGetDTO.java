package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.Date;

public class UserRankingGetDTO {
    private Long userId;
    private String username;
    private Date createDay;
    private long score;
    private long gameNum;

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public Date getCreateDay() {return createDay;}
    public void setCreateDay(Date createDay) {this.createDay = createDay;}

    public long getScore() {return score;}
    public void setScore(long score) {this.score = score;}

    public long getGameNum() {return gameNum;}
    public void setGameNum(long gameNum) {this.gameNum = gameNum;}
}

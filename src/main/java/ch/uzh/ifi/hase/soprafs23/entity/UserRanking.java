package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.Date;

public class UserRanking {
    private Long userId;
    private String username;
    private Date createDay;
    private long score;
    private long gameNum;

    public UserRanking(Long id, String username, Date createDay, long score, long gameNum) {
        this.userId = id;
        this.username = username;
        this.createDay = createDay;
        this.score = score;
        this.gameNum = gameNum;
    }

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

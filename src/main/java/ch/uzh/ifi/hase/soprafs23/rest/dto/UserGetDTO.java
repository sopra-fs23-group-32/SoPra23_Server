package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import javax.persistence.Column;
import java.util.Date;

public class UserGetDTO {

    private Long userId;
    private String username;
    private String password;
    private UserStatus status;
    private Date createDay;
    private Date birthDay = null;

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String pwd) {this.password = pwd;}

    public UserStatus getStatus() {return status;}
    public void setStatus(UserStatus status) {this.status = status;}

    public Date getCreateDay() {return createDay;}
    public void setCreateDay(Date createDay) {this.createDay = createDay;}

    public Date getBirthDay() {return birthDay;}
    public void setBirthDay(Date birthDay) {this.birthDay = birthDay;}
}

package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.Date;

public class UserPutDTO {
    private String username;
    private String password = null;
    private Date birthDay = null;

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String pwd) {this.password = pwd;}

    public Date getBirthDay() {return birthDay;}
    public void setBirthDay(Date birthDay) {this.birthDay = birthDay;}
}

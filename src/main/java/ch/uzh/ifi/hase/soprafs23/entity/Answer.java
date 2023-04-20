package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;


public class Answer {

    private String answer;
    private int timeTaken;

//    public Long getId() {return id;}
//    public void setId(Long id) {this.id = id;}
    
    public String getAnswer() {return answer;}
    public void setAnswer(String answer) {this.answer = answer;}

    public int getTimeTaken() {return timeTaken;}
    public void setTimeTaken(int timeTaken) {this.timeTaken = timeTaken;}
}

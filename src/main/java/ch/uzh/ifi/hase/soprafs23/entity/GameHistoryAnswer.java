package ch.uzh.ifi.hase.soprafs23.entity;

public class GameHistoryAnswer {
    String answer;
    String correctAnswer;

    public GameHistoryAnswer(String answer, String label) {
        this.answer = answer;
        this.correctAnswer = label;
    }

    public String getAnswer() {return answer;}
    public void setAnswer(String answer) {this.answer = answer;}

    public String getCorrectAnswer() {return correctAnswer;}
    public void setCorrectAnswer(String correctAnswer) {this.correctAnswer = correctAnswer;}
}

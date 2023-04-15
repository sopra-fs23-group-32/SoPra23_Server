package ch.uzh.ifi.hase.soprafs23.entity;

public class Answer {
    private Long playerId;
    private String answer;
    private int timeTaken;

    public Answer(Long playerId, String answer, int timeTaken) {
        this.playerId = playerId;
        this.answer = answer;
        this.timeTaken = timeTaken;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }
}

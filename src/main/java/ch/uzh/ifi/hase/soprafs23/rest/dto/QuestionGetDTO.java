package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class QuestionGetDTO {
    String option1;
    String option2;
    String option3;
    String option4;
    String pictureUrl;

    public String getOption1() {return option1;}
    public void setOption1(String option) {this.option1 = option;}

    public String getOption2() {return option2;}
    public void setOption2(String option) {this.option2 = option;}

    public String getOption3() {return option3;}
    public void setOption3(String option) {this.option3 = option;}

    public String getOption4() {return option4;}
    public void setOption4(String option) {this.option4 = option;}

    public String getPictureUrl() {return pictureUrl;}
    public void setPictureUrl(String pictureUrl) {this.pictureUrl = pictureUrl;}
}

package ch.uzh.ifi.hase.soprafs23.entity;

public class Question {
    String option1;
    String option2;
    String option3;
    String option4;
    String correctOption;
    String pictureUrl;

    public Question(String option1, String option2, String option3,
                    String option4, String correctOption, String pictureUrl) {
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctOption = correctOption;
        this.pictureUrl = pictureUrl;
    }

    public String getOption1() {return option1;}
    public String getOption2() {return option2;}
    public String getOption3() {return option3;}
    public String getOption4() {return option4;}
    public String getCorrectOption() {return correctOption;}
    public String getPictureUrl() {
        return pictureUrl;
    }
}

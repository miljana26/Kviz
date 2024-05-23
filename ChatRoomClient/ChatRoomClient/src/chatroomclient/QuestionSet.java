package chatroomclient;

import java.util.ArrayList;

public class QuestionSet {

    private ArrayList<Question> questions;
    private int questionSetNumber;

    public QuestionSet(int questionSetNumber) {
        this.questions = new ArrayList<>();
        this.questionSetNumber = questionSetNumber;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

}

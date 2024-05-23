package chatroomclient;

public class Question {

    private int questionNumber;
    private String[] answers;
    private String correctAnswer;
    private String Question;

    public Question(String[] answers, String correctAnswer, String Question, int questionNumber) {
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.Question = Question;
        this.questionNumber = questionNumber;

    }

    public String[] getAnswers() {
        return answers;
    }

    public boolean IsAnswerCorrect(String insertedAnswer) {
        return insertedAnswer.equals(correctAnswer);
    }

}

package chatroomserver;

public class Question {

    private int questionNumber;
    private String[] answers;
    private String[] wrongAnswers;
    private String correctAnswer;
    private String Question;

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String[] getWrongAnswers() {
        return wrongAnswers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getQuestion() {
        return Question;
    }

    public Question(String[] answers, String correctAnswer, String Question, int questionNumber) {
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.Question = Question;
        this.questionNumber = questionNumber;
        this.wrongAnswers = new String[2];
        this.wrongAnswers[0] = answers[0];
        this.wrongAnswers[1] = answers[1];

    }

    public String[] getAnswers() {
        return answers;
    }

    public boolean IsAnswerCorrect(String insertedAnswer) {
        return insertedAnswer.equals(correctAnswer);
    }

}

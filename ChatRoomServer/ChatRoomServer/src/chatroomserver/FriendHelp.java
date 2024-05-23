package chatroomserver;

public class FriendHelp {

    private String WhoAskedForHelp;
    private String AnswerGiver;
    private String Answer;
    private String Question;

    public String getWhoAskedForHelp() {
        return WhoAskedForHelp;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String Question) {
        this.Question = Question;
    }

    public void setWhoAskedForHelp(String WhoAskedForHelp) {
        this.WhoAskedForHelp = WhoAskedForHelp;
    }

    public String getAnswerGiver() {
        return AnswerGiver;
    }

    public void setAnswerGiver(String AnswerGiver) {
        this.AnswerGiver = AnswerGiver;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String Answer) {
        this.Answer = Answer;
    }

    public FriendHelp(String WhoAskedForHelp, String AnswerGiver, String Answer, String Question) {
        this.WhoAskedForHelp = WhoAskedForHelp;
        this.AnswerGiver = AnswerGiver;
        this.Answer = Answer;
        this.Question = Question;
    }

}

package chatroomserver;

public class HelpUsedByContestantInSet {

    private int set;
    private int question;
    private String contestant;
    private String help;

    public int getSet() {
        return set;
    }

    public String getContestant() {
        return contestant;
    }

    public String getHelp() {
        return help;
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public HelpUsedByContestantInSet(int set, int question, String contestant, String help) {
        this.set = set;
        this.question = question;
        this.contestant = contestant;
        this.help = help;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public void setContestant(String contestant) {
        this.contestant = contestant;
    }

    public void setHelp(String help) {
        this.help = help;
    }

}

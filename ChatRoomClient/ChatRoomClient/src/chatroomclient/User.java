package chatroomclient;

public class User {

    private String username;

    private TypeOfUser typeOfUser;
    private int totalAnsweredQuestions;
    private int correctAnsweredQuestions;
    private int currentQuestionInSet;
    private int currentSet;

    public User(String username, String typeOfUser, int correctAnsweredQuestions, int totalAnsweredQuestions, int currentQuestionInSet, int currentSet) {
        this.username = username;

        if (typeOfUser.equals("admin")) {
            this.typeOfUser = TypeOfUser.ADMIN;
        } else {
            this.typeOfUser = TypeOfUser.CONTESTANT;
        }
        this.totalAnsweredQuestions = totalAnsweredQuestions;
        this.correctAnsweredQuestions = correctAnsweredQuestions;
        this.currentQuestionInSet = currentQuestionInSet;
        this.currentSet = currentSet;
    }

    public User(String username, String userType) {
        this.username = username;
        if (userType.equals("admin")) {
            this.typeOfUser = TypeOfUser.ADMIN;
        } else {
            this.typeOfUser = TypeOfUser.CONTESTANT;
        }

    }

    public enum TypeOfUser {
        CONTESTANT,
        ADMIN
    }

    public String getUsername() {
        return username;
    }

    public TypeOfUser getTypeOfUser() {
        return typeOfUser;
    }

    public String getTypeOfUserToString() {
        if (this.typeOfUser == TypeOfUser.ADMIN) {
            return "admin";
        }
        return "contestant";
    }

    public int getTotalAnsweredQuestions() {
        return totalAnsweredQuestions;
    }

    public int getCorrectAnsweredQuestions() {
        return correctAnsweredQuestions;
    }

    public int getCurrentQuestionInSet() {
        return currentQuestionInSet;
    }

    public int getCurrentSet() {
        return currentSet;
    }

    public void setTypeOfUser(TypeOfUser typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public void setTotalAnsweredQuestions(int totalAnsweredQuestions) {
        this.totalAnsweredQuestions = totalAnsweredQuestions;
    }

    public void setCorrectAnsweredQuestions(int correctAnsweredQuestions) {
        this.correctAnsweredQuestions = correctAnsweredQuestions;
    }

    public void setCurrentQuestionInSet(int currentQuestionInSet) {
        this.currentQuestionInSet = currentQuestionInSet;
    }

}

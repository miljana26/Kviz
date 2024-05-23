package chatroomserver;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;

public class ServerController {

    static final String HELP_HALF_HALF = "help_half_half";
    static final String HELP_ASK_FRIEND = "help_ask_friend";
    static final String HELP_CHANGE_QUESTION = "help_change_question";

    public static void HandleRequest(String request, PrintWriter pw) {
        try {
            String[] split = request.split("\n", 2);
            String method = split[0];
            String body = (split.length > 1) ? split[1].trim() : " ";
            switch (method) {
                //All
                case "Login":
                    Login(body, pw);
                    break;
                //Admin
                case "AddUser":
                    AddUser(body, pw);
                    break;
                case "RemoveUser":
                    RemoveUser(body, pw);
                    break;
                case "SetActiveSet":
                    SetActiveSet(body, pw);
                    break;
                case "GetActiveSet":
                    GetActiveSet(body, pw);
                    break;
                case "GetAllUsers":
                    GetAllUsers(body, pw);
                    break;
                //Contestant
                case "GetTableResults":
                    GetTableResults(body, pw);
                    break;
                case "GetCurrentQuestion":
                    GetCurrentQuestion(body, pw);
                    break;
                case "AnswerCurretnQuestion":
                    AnswerCurretnQuestion(body, pw);
                    break;
                case "HelpFriend":
                    HelpFriend(body, pw);
                    break;
                case "FriendsInNeedOfHelp":
                    FriendsInNeedOfHelp(body, pw);
                    break;
                case "AskForHelpFromFriend":
                    AskForHelpFromFriend(body, pw);
                    break;
                case "GetHelpFromFriends":
                    GetHelpFromFriends(body, pw);
                    break;
                case "SwitchQuestion":
                    SwitchQuestion(body, pw);
                    break;
                case "HalfHalfQuestion":
                    HalfHalfQuestion(body, pw);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void Login(String body, PrintWriter pw) {

        try {
            String[] loginInfo = body.split(":");
            String username = loginInfo[0];
            String password = loginInfo[1];
            User.TypeOfUser typeOfUser = loginInfo[2].equals("admin") ? User.TypeOfUser.ADMIN : User.TypeOfUser.CONTESTANT;

            boolean found = false;
            for (User user : DataBase.Users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.getTypeOfUser() == typeOfUser) {
                    found = true;
                    break;
                }
            }
            if (found) {
                pw.println(loginInfo[2] + ":" + loginInfo[0]);
            } else {
                pw.println("Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RemoveUser(String body, PrintWriter pw) {

        DataBase.DeleteByUsername(body);
        pw.println("Done");
    }

    private static void SetActiveSet(String body, PrintWriter pw) {
        DataBase.SetActiveSet(Integer.parseInt(body));
        pw.println("Done");
    }

    private static void AddUser(String body, PrintWriter pw) {
        try {
            String[] userInfo = body.split(":");
            String username = userInfo[0];
            String password = userInfo[1];
            String userType = userInfo[2];
            if (DataBase.AddUser(new User(username, password, userType, 0, 0, 1, 0))) {
                pw.println("SUCCESS");
            } else {
                pw.println("FAILED");
            }

        } catch (Exception e) {
            e.printStackTrace();
            pw.println("FAILED");
        }
    }

    private static void GetTableResults(String body, PrintWriter pw) {
        User user = DataBase.FindUser(body.trim());
        if (user == null) {
            pw.println("USER NOT FOUND");
            return;
        }

        StringBuilder sb = new StringBuilder("");

        Collections.sort(DataBase.Users, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return Integer.compare(user2.getCorrectAnsweredQuestions(), user1.getCorrectAnsweredQuestions());
            }
        });

        for (User u : DataBase.Users) {
            if (u.getTypeOfUser().equals(User.TypeOfUser.CONTESTANT)) {
                sb.append(u.getUsername()).append(":").append(u.getTypeOfUserToString()).append(":").append(u.getCorrectAnsweredQuestions()).append(":").append(u.getTotalAnsweredQuestions()).append(":").append(u.getCurrentQuestionInSet()).append(":").append(u.getCurrentSet()).append("\n");
            }

        }
        pw.println(sb.toString());
    }

    private static void GetCurrentQuestion(String body, PrintWriter pw) {
        User user = DataBase.FindUser(body);
        if (user == null) {
            pw.println("USER NOT FOUND");
            return;
        }
        int ActiveSet = DataBase.ActiveSet;
        if (ActiveSet != user.getCurrentSet()) {
            user.setCurrentSet(ActiveSet);
            user.setCurrentQuestionInSet(1);
        }
        DataBase.SaveUsers();
        Question question = DataBase.GetQuestion(user.getCurrentSet(), user.getCurrentQuestionInSet());
        if (question == null || question.getQuestionNumber() == 11) {
            pw.println("NO QUESTION");
            return;
        }

        try {
            StringBuilder sb = new StringBuilder("");
            sb.append(new String(question.getQuestion().getBytes(StandardCharsets.UTF_8), "UTF-8")).append("\n");
            for (String s : question.getAnswers()) {
                sb.append(new String(s.getBytes(StandardCharsets.UTF_8), "UTF-8")).append("\n");
            }
            pw.println(sb);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            pw.println("ERROR: Unsupported Encoding");
        }
    }

    private static void AnswerCurretnQuestion(String body, PrintWriter pw) {
        try {

            User user = DataBase.FindUser(body.split("\n")[0]);
            if (user == null) {
                pw.println("USER NOT FOUND");
                return;
            }
            Question question = DataBase.getQuestionByQuestionText(body.split("\n")[1]);
            if (question == null) {
                pw.println("QUESTION NOT FOUND");
                return;
            }

            if (question.IsAnswerCorrect(body.split("\n")[2])) {
                user.setCorrectAnsweredQuestions(user.getCorrectAnsweredQuestions() + 1);
                pw.println("CORRECT");
            } else {
                pw.println("The correct answer was: " + question.getCorrectAnswer());
            }
            user.setCurrentQuestionInSet(user.getCurrentQuestionInSet() + 1);
            user.setTotalAnsweredQuestions(user.getTotalAnsweredQuestions() + 1);

            DataBase.SaveUsers();

        } catch (Exception e) {
            pw.println("ERROR");
        }
    }

    private static void HelpFriend(String body, PrintWriter pw) {
        try {
            User user = DataBase.FindUser(body.split("\n")[0]);
            if (user == null) {
                pw.println("USER NOT FOUND");
                return;
            }
            FriendHelp friendHelp = DataBase.FindQuestionToAnswer(user.getUsername(), body.split("\n")[1], body.split("\n")[2]);

            if (friendHelp == null) {
                pw.println("FriendHelp NOT FOUND");
                return;
            }
            friendHelp.setAnswer(body.split("\n")[3]);
            DataBase.SaveFriendHelps();

            pw.println("SUCCESS");

        } catch (Exception e) {
            pw.println("ERROR");
        }
    }

    private static void FriendsInNeedOfHelp(String body, PrintWriter pw) {
        try {
            User user = DataBase.FindUser(body);
            if (user == null) {
                pw.println("USER NOT FOUND");
                return;
            }

            StringBuilder sb = new StringBuilder("");
            for (FriendHelp fh : DataBase.getUnAnsweredQuestions(user.getUsername())) {
                sb.append(fh.getWhoAskedForHelp()).append("__").append(fh.getAnswerGiver()).append("__").append(fh.getAnswer()).append("__").append(fh.getQuestion()).append("\n");
            }
            pw.println(sb);

        } catch (Exception e) {
            pw.println("ERROR");
        }
    }

    private static void AskForHelpFromFriend(String body, PrintWriter pw) {
        try {

            User user = DataBase.FindUser(body.split(":")[0]);
            if (user == null) {
                pw.println("USER NOT FOUND");
                return;
            }
            User friend = DataBase.FindUser(body.split(":")[1]);
            if (friend == null) {
                pw.println("FRIEND NOT FOUND");
                return;
            }
            HelpUsedByContestantInSet help = new HelpUsedByContestantInSet(user.getCurrentSet(), user.getCurrentQuestionInSet(), user.getUsername(), HELP_ASK_FRIEND);
            if (DataBase.UseHelp(help)) {
                StringBuilder sb = new StringBuilder("");
                Question question = DataBase.GetQuestion(user.getCurrentSet(), user.getCurrentQuestionInSet());
                if (question == null) {
                    pw.println("NO QUESTION");
                    return;
                }
                DataBase.AddFriendHelp(new FriendHelp(user.getUsername(), body.split(":")[1], "EMPTY", question.getQuestion()));
                pw.println("SUCCESS");

            } else {
                pw.println("CAN NOT USE THIS HELP AT THIS MOMENT!");
            }

        } catch (Exception e) {
            pw.println("ERROR");
        }
    }

    private static void GetHelpFromFriends(String body, PrintWriter pw) {

        try {
            User user = DataBase.FindUser(body);
            if (user == null) {
                pw.println("USER NOT FOUND");
                return;
            }

            StringBuilder sb = new StringBuilder("");
            for (FriendHelp fh : DataBase.getHelpAskedByContestant(user.getUsername())) {
                sb.append(fh.getWhoAskedForHelp()).append("__").append(fh.getAnswerGiver()).append("__").append(fh.getAnswer()).append("__").append(fh.getQuestion()).append("\n");
            }
            pw.println(sb);

        } catch (Exception e) {
            pw.println("ERROR");
        }
    }

    private static void SwitchQuestion(String body, PrintWriter pw) {
        try {

            User user = DataBase.FindUser(body);
            if (user == null) {
                pw.println("USER NOT FOUND");
                return;
            }
            HelpUsedByContestantInSet help = new HelpUsedByContestantInSet(user.getCurrentSet(), user.getCurrentQuestionInSet(), user.getUsername(), HELP_CHANGE_QUESTION);
            if (DataBase.UseHelp(help)) {
                Question question = DataBase.GetQuestion(user.getCurrentSet(), 11);
                if (question == null) {
                    pw.println("NO QUESTION");
                    return;
                }

                StringBuilder sb = new StringBuilder("");
                sb.append(question.getQuestion()).append("\n");
                for (String s : question.getAnswers()) {
                    sb.append(s).append("\n");
                }
                pw.println(sb);

            } else {
                pw.println("CAN NOT USE THIS HELP AT THIS MOMENT!");
            }

        } catch (Exception e) {
            pw.println("ERROR");
        }

    }

    private static void HalfHalfQuestion(String body, PrintWriter pw) {

        try {

            User user = DataBase.FindUser(body);
            if (user == null) {
                pw.println("USER NOT FOUND");
                return;
            }
            HelpUsedByContestantInSet help = new HelpUsedByContestantInSet(user.getCurrentSet(), user.getCurrentQuestionInSet(), user.getUsername(), HELP_HALF_HALF);
            if (DataBase.UseHelp(help)) {
                StringBuilder sb = new StringBuilder("");
                Question question = DataBase.GetQuestion(user.getCurrentSet(), user.getCurrentQuestionInSet());
                if (question == null) {
                    pw.println("NO QUESTION");
                    return;
                }
                for (String s : question.getWrongAnswers()) {
                    sb.append(s).append("\n");
                }
                pw.println(sb);

            } else {
                pw.println("CAN NOT USE THIS HELP AT THIS MOMENT!");
            }

        } catch (Exception e) {
            pw.println("ERROR");
        }
    }

    private static void GetAllUsers(String body, PrintWriter pw) {
        StringBuilder sb = new StringBuilder("");
        for (User u : DataBase.Users) {
            sb.append(u.getUsername()).append(":").append(u.getTypeOfUserToString()).append("\n");
            ;
        }
        pw.println(sb.toString());
    }

    private static void GetActiveSet(String body, PrintWriter pw) {
        StringBuilder sb = new StringBuilder("");
        for (int u : DataBase.LoadActiveSets()) {
            sb.append(u).append("\n");
            ;
        }
        pw.println(sb.toString());

    }

}

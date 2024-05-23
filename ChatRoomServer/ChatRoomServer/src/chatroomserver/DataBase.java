package chatroomserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataBase {

    public static ArrayList<User> Users = new ArrayList<>();
    public static ArrayList<QuestionSet> QuestionSets = new ArrayList<>();
    public static ArrayList<HelpUsedByContestantInSet> HelpsUsedByContestantsInSets = new ArrayList<>();
    public static ArrayList<FriendHelp> FriendHelps = new ArrayList<>();
    public static int ActiveSet;
    private static SecretKey symmetricKey = createAESKey();
    private static byte[] initializationVector = createInitializationVector();
    private static final String FILE_NAME = "src/chatRoomServer/Resources/FriendHelps.xml";

    public static void LoadData() {
        LoadQuestionSets();
        LoadUsers();
        LoadActiveSets();
        LoadHelpsUsedByContestantsInSets();
        LoadFriendHelps();

    }

    public static User FindUser(String username) {

        for (User user : DataBase.Users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public static void LoadQuestionSets() {

        for (int j = 1; j <= 4; j++) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/chatRoomServer/Resources/set" + j + ".txt"), StandardCharsets.UTF_8))) {
                String line;
                int questionNumber = 0;
                String question = "";
                String[] answers = new String[4];
                QuestionSet questionSet = new QuestionSet(j);

                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    if (Character.isDigit(line.charAt(0))) {
                        if (!question.isEmpty()) {
                            questionSet.getQuestions().add(new Question(answers, answers[3], question, questionNumber));
                            question = "";
                        }
                        questionNumber++;
                        question = "Question " + questionNumber + ": " + new String(line.substring(line.indexOf(' ') + 1).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                        answers = new String[4];
                    } else {
                        char answerLetter = line.charAt(0);
                        String answerText = new String(line.substring(line.indexOf(' ') + 1).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                        int index = answerLetter - 'a';
                        answers[index] = answerText;
                    }
                }
                if (!question.isEmpty()) {
                    questionSet.getQuestions().add(new Question(answers, answers[3], question, questionNumber));
                }
                QuestionSets.add(questionSet);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void LoadUsers() {
        try {
            String fileName = "src/chatRoomServer/Resources/users.txt";
            Users.clear();

            try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
                byte[] encryptedData = new byte[dis.available()];
                dis.readFully(encryptedData);
                String decryptedData = do_AESDecryption(encryptedData, symmetricKey, initializationVector);
                String[] allUsersData = decryptedData.split("\n");
                for (String userData : allUsersData) {
                    String[] splitUserData = userData.split(":");
                    Users.add(new User(splitUserData[0], splitUserData[1], splitUserData[2].trim(), Integer.parseInt(splitUserData[3].trim()), Integer.parseInt(splitUserData[4].trim()), Integer.parseInt(splitUserData[5].trim()), Integer.parseInt(splitUserData[6].trim())));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void SaveUsers() {
        try {
            String fileName = "src/chatRoomServer/Resources/users.txt";
            StringBuilder stringBuilder = new StringBuilder();

            for (User user : Users) {
                stringBuilder.append(user.getUsername()).append(":").append(user.getPassword()).append(":").append(user.getTypeOfUser().equals(User.TypeOfUser.ADMIN) ? "admin" : "contestant").append(":");
                stringBuilder.append(user.getCorrectAnsweredQuestions()).append(":").append(user.getTotalAnsweredQuestions()).append(":").append(user.getCurrentQuestionInSet()).append(":").append(user.getCurrentSet());
                stringBuilder.append("\n");
            }

            String dataToEncrypt = stringBuilder.toString();
            byte[] encryptedData = do_AESEncryption(dataToEncrypt, symmetricKey, initializationVector);
            try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {
                dos.write(encryptedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final String AES = "AES";

    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";

    private static Scanner message;

    public static SecretKey createAESKey() {
        try {
            SecureRandom secureRandom = new SecureRandom("RSZEOS2024".getBytes());
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(128, secureRandom);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] createInitializationVector() {
        byte[] initializationVector = new byte[16];
        for (int i = 0; i < 16; i++) {
            initializationVector[i] = (byte) (i + 1); // Elements from 1 to 16
        }
        return initializationVector;
    }

    public static byte[] do_AESEncryption(String plainText, SecretKey secretKey, byte[] initializationVector) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(plainText.getBytes());
    }

    public static String do_AESDecryption(byte[] cipherText, SecretKey secretKey, byte[] initializationVector) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] result = cipher.doFinal(cipherText);

        return new String(result);
    }

    public static boolean AddUser(User user) {
        String password = user.getPassword();
        String username = user.getUsername();
        if (username.matches("^\\d.*") || !username.matches("^[a-zA-Z0-9]*$")) {
            return false;
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$")) {
            return false;
        }

        for (User existingUser : Users) {
            if (existingUser.getUsername().equals(username)) {
                return false;
            }
        }

        Users.add(user);
        SaveUsers();
        LoadUsers();

        return true;
    }

    public static void DeleteByUsername(String username) {
        for (User user : Users) {
            if (user.getUsername().equals(username)) {
                Users.remove(user);
                break;
            }
        }
        SaveUsers();
        LoadUsers();

    }

    public static List<Integer> LoadActiveSets() {
        String fileName = "src/chatRoomServer/Resources/activeSet.txt";
        List<Integer> activeSets = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                activeSets.add(Integer.parseInt(line.trim()));
            }
        } catch (FileNotFoundException e) {

            activeSets.add(1);
            SaveActiveSet(activeSets);

        } catch (IOException e) {
            e.printStackTrace();
        }
        ActiveSet = activeSets.get(activeSets.size() - 1);

        return activeSets;

    }

    private static void SaveActiveSet(List<Integer> activeSets) {
        String fileName = "src/chatRoomServer/Resources/activeSet.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int activeSet : activeSets) {
                writer.write(Integer.toString(activeSet));
                writer.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean SetActiveSet(int set) {
        List<Integer> activeSets = LoadActiveSets();
        if (activeSets.contains(set)) {
            return false;
        }
        ActiveSet = set;
        activeSets.add(set);
        SaveActiveSet(activeSets);
        LoadActiveSets();
        return true;
    }

    private static void createEmptyFriendHelpsFile(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("FriendHelps");
            doc.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(FILE_NAME));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void LoadFriendHelps() {
        FriendHelps.clear();
        try {
            File file = new File(FILE_NAME);
            if (!file.exists() || file.length() == 0) {
                createEmptyFriendHelpsFile(file);
                return;
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("FriendHelp");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String whoAskedForHelp = element.getElementsByTagName("WhoAskedForHelp").item(0).getTextContent();
                    String answerGiver = element.getElementsByTagName("AnswerGiver").item(0).getTextContent();
                    String answer = element.getElementsByTagName("Answer").item(0).getTextContent();
                    String question = element.getElementsByTagName("Question").item(0).getTextContent();
                    FriendHelps.add(new FriendHelp(whoAskedForHelp, answerGiver, answer, question));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void SaveFriendHelps() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("FriendHelps");
            doc.appendChild(rootElement);

            for (FriendHelp help : FriendHelps) {
                Element friendHelpElement = doc.createElement("FriendHelp");
                rootElement.appendChild(friendHelpElement);

                Element whoAskedForHelp = doc.createElement("WhoAskedForHelp");
                whoAskedForHelp.appendChild(doc.createTextNode(help.getWhoAskedForHelp()));
                friendHelpElement.appendChild(whoAskedForHelp);

                Element answerGiver = doc.createElement("AnswerGiver");
                answerGiver.appendChild(doc.createTextNode(help.getAnswerGiver()));
                friendHelpElement.appendChild(answerGiver);

                Element answer = doc.createElement("Answer");
                answer.appendChild(doc.createTextNode(help.getAnswer()));
                friendHelpElement.appendChild(answer);

                Element question = doc.createElement("Question");
                question.appendChild(doc.createTextNode(help.getQuestion()));
                friendHelpElement.appendChild(question);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Enable indentation
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Set indentation amount

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(FILE_NAME));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void SaveHelpsUsedByContestantsInSets() {
        String fileName = "src/chatRoomServer/Resources/helpUsed.txt";

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            for (HelpUsedByContestantInSet help : HelpsUsedByContestantsInSets) {
                String line = help.getSet() + ":" + help.getQuestion() + ":" + help.getContestant() + ":" + help.getHelp();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void AddHelpUsedByContestantInSet(HelpUsedByContestantInSet helpUsedByContestantInSet) {
        HelpsUsedByContestantsInSets.add(helpUsedByContestantInSet);
        SaveHelpsUsedByContestantsInSets();
    }

    public static HelpUsedByContestantInSet HasUsedHelpUsedByContestantInSet(HelpUsedByContestantInSet helpUsedByContestantInSet) {
        for (HelpUsedByContestantInSet help : HelpsUsedByContestantsInSets) {
            if (help.getSet() == helpUsedByContestantInSet.getSet()
                    && help.getContestant().equals(helpUsedByContestantInSet.getContestant())
                    && help.getHelp().equals(helpUsedByContestantInSet.getHelp())) {
                return help;
            }
        }
        return null;
    }

    public static boolean UseHelp(HelpUsedByContestantInSet helpUsedByContestantInSet) {
        HelpUsedByContestantInSet help = HasUsedHelpUsedByContestantInSet(helpUsedByContestantInSet);

        if (help == null) {
            HelpsUsedByContestantsInSets.add(helpUsedByContestantInSet);
            SaveHelpsUsedByContestantsInSets();
            return true;
        }
        if (help.getQuestion() == helpUsedByContestantInSet.getQuestion()) {
            return true;
        }

        return false;
    }

    public static void LoadHelpsUsedByContestantsInSets() {
        String fileName = "src/chatRoomServer/Resources/helpUsed.txt";
        ArrayList<HelpUsedByContestantInSet> helpsUsedByContestantsInSets = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 4) {
                    int set = Integer.parseInt(parts[0]);
                    int question = Integer.parseInt(parts[1]);
                    String contestant = parts[2];
                    String help = parts[3];
                    helpsUsedByContestantsInSets.add(new HelpUsedByContestantInSet(set, question, contestant, help));
                } else {
                    // Handle invalid line format
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HelpsUsedByContestantsInSets = helpsUsedByContestantsInSets;
    }

    public static Question getQuestionByQuestionText(String questionText) {
        for (QuestionSet qs : QuestionSets) {
            for (Question q : qs.getQuestions()) {
                if (q.getQuestion().equals(questionText)) {
                    return q;
                }
            }
        }
        return null;
    }

    public static Question GetQuestion(int set, int questionNumber) {
        QuestionSet questionSet = null;
        for (QuestionSet qs : QuestionSets) {
            if (qs.getQuestionSetNumber() == set) {
                questionSet = qs;
                break;
            }
        }
        if (questionSet == null) {
            return null;
        }

        Question question = null;
        for (Question q : questionSet.getQuestions()) {
            if (q.getQuestionNumber() == questionNumber) {
                question = q;
                break;
            }
        }

        return question;
    }

    static ArrayList<FriendHelp> getHelpAskedByContestant(String username) {
        ArrayList<FriendHelp> friendHelps = new ArrayList<>();
        for (FriendHelp fh : FriendHelps) {
            if (fh.getWhoAskedForHelp().equals(username) && !fh.getAnswer().equals("EMPTY")) {
                friendHelps.add(fh);
            }
        }
        return friendHelps;
    }

    static ArrayList<FriendHelp> getUnAnsweredQuestions(String username) {
        ArrayList<FriendHelp> friendHelps = new ArrayList<>();
        for (FriendHelp fh : FriendHelps) {
            if (fh.getAnswerGiver().equals(username) && fh.getAnswer().equals("EMPTY")) {
                friendHelps.add(fh);
            }
        }
        return friendHelps;

    }

    static void AddFriendHelp(FriendHelp friendHelp) {
        boolean duplicateExists = false;
        for (FriendHelp existingHelp : FriendHelps) {
            if (existingHelp.getWhoAskedForHelp().equals(friendHelp.getWhoAskedForHelp())
                    && existingHelp.getAnswerGiver().equals(friendHelp.getAnswerGiver())
                    && existingHelp.getQuestion().equals(friendHelp.getQuestion())) {
                duplicateExists = true;
                break;
            }
        }

        if (!duplicateExists) {
            FriendHelps.add(friendHelp);
            SaveFriendHelps();
            LoadFriendHelps();
        }
    }

    static FriendHelp FindQuestionToAnswer(String username, String whoAsked, String question) {
        for (FriendHelp existingHelp : FriendHelps) {
            if (existingHelp.getWhoAskedForHelp().equals(whoAsked)
                    && existingHelp.getAnswerGiver().equals(username)
                    && existingHelp.getQuestion().equals(question)) {
                return existingHelp;
            }
        }
        return null;
    }

}

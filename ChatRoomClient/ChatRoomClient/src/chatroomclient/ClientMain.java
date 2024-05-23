package chatroomclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;

public class ClientMain {

    private static Socket socket;
    private static BufferedReader br;
    public static PrintWriter pw;
    public static String currentUser = "";

    public static void main(String args[]) {

        System.out.println("----------------- CLIENT STARTED -----------------");
        try {
            socket = new Socket("127.0.0.1", 6001);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Unable to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        new LoginWindow().setVisible(true);
    }

    public static String SendRequestAndRecieveResponse(String request) {
        StringBuilder messageBuilder = new StringBuilder();
        try {
            pw.println(request);
            while (true) {
                int character = br.read();
                if (character == -1) {
                    break;
                }
                messageBuilder.append((char) character);
                if (messageBuilder.toString().endsWith("\r\n")) {
                    String message = messageBuilder.substring(0, messageBuilder.length() - 2); // Remove the "\r\n"
                    if (message.equals("USER NOT FOUND")) {
                        JOptionPane.showMessageDialog(null, "The user that logged in with this credentials no longer exists", "USER NOT FOUND", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                    return messageBuilder.toString().trim();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return messageBuilder.toString();
    }

}

package chatroomserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatRoomServer {

    private ServerSocket ssocket;
    private int port;
    private ArrayList<ConnectedChatRoomClient> clients;

    public ServerSocket getSsocket() {
        return ssocket;
    }

    public void setSsocket(ServerSocket ssocket) {
        this.ssocket = ssocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void acceptClients() {
        Socket client = null;
        Thread thr;
        while (true) {
            try {
                client = this.ssocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(ChatRoomServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (client != null) {
                ConnectedChatRoomClient clnt = new ConnectedChatRoomClient(client, clients);
                clients.add(clnt);
                thr = new Thread(clnt);
                thr.start();
            } else {
                break;
            }
        }
    }

    public ChatRoomServer(int port) {
        this.clients = new ArrayList<>();
        try {
            this.port = port;
            this.ssocket = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ChatRoomServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        System.out.println("----------------- SERVER STARTED -----------------");
        DataBase.LoadData();
        ChatRoomServer server = new ChatRoomServer(6001);
        server.acceptClients();

    }

}

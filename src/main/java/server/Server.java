package server;

import common.Command;
import common.Command.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

;

public class Server {
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream ous;

    public void run() throws IOException {

        ServerSocket server = new ServerSocket(8189);
        System.out.println("Server started...");

        while (true) {
            try {
                this.socket = server.accept();
                System.out.println("Server connected with client...");
                is = new DataInputStream(socket.getInputStream());
                ous = new DataOutputStream(socket.getOutputStream());

                ous.writeUTF(Command.START.getCommand());

                new Thread(new ClientHandler(socket)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

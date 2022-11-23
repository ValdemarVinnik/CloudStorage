package server;

import common.Command;
import common.Command.*;
import common.model.User;
import lombok.extern.slf4j.Slf4j;
import server.db.DBConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


@Slf4j
public class Server {
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream ous;
    private ObjectInputStream ois;
    private DBConnection dbConnection;

    public Server() {
        dbConnection = new DBConnection();
    }

    public void run() throws IOException {

        ServerSocket server = new ServerSocket(8189);
        log.info("Server started...");

        while (true) {
            try {
                this.socket = server.accept();
                log.info("Server started...");
                is = new DataInputStream(socket.getInputStream());
                ous = new DataOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());

                ous.writeUTF(Command.START.getCommand());

                readLoginCommands();


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    void readLoginCommands() throws IOException, ClassNotFoundException {
        while (true){
            String massage = is.readUTF();

            if(massage.equals(Command.REG.getCommand())){
                User user = (User)ois.readObject();
                if(dbConnection.getUserByLogin(user.getLogin()) == null){
                    startNewClientHandler(user);
                }
            }

            if(massage.equals(Command.AUTH.getCommand())){
                User user = (User)ois.readObject();
                if(dbConnection.getUserByLoginAndPassword(user.getLogin(), user.getPassword()) == null){
                    startNewClientHandler(user);
                }

            }
        }
    }

    private void startNewClientHandler(User user) throws IOException {
        Thread thread = new Thread(new ClientHandler(socket, user));
        thread.setDaemon(true);
        thread.start();
    }

    ;
}

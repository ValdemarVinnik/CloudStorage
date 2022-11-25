package server;

import common.Command;
import common.Command.*;
import common.model.User;
import lombok.extern.slf4j.Slf4j;
import server.db.DBConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


@Slf4j
public class Server {
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream ous;
    private ObjectInputStream ois;
    //private DBConnection dbConnection;

    public Server() {

    }

    public void run() throws IOException {

        ServerSocket server = new ServerSocket(8189);
        log.info("Server started...");

        while (true) {
            try {
                this.socket = server.accept();
                log.info("Server connect...");
                //is = new DataInputStream(socket.getInputStream());
                //ous = new DataOutputStream(socket.getOutputStream());
                //ois = new ObjectInputStream(socket.getInputStream());

                //ous.writeUTF(Command.START.getCommand());

               // readLoginCommands();
                Thread thread = new Thread(new ClientHandler(socket));
                thread.setDaemon(true);
                thread.start();


            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }

//    void readLoginCommands() throws IOException, ClassNotFoundException {
//        while (true) {
//            String massage = is.readUTF();
//
//            if (massage.equals(Command.REG.getCommand())) {
//                ois = new ObjectInputStream(socket.getInputStream());
//                User user = (User) ois.readObject();
//                dbConnection.registerUser(user);
//                    createNewUsersFolder(user);
//                    startNewClientHandler(user);
//
//                    // ous. error
//
//            }
//
//            if (massage.equals(Command.AUTH.getCommand())) {
//                User user = (User) ois.readObject();
//                if (dbConnection.getUserByLoginAndPassword(user.getLogin(), user.getPassword()) == null) {
//                    startNewClientHandler(user);
//                }
//
//            }
//        }
//    }

    private boolean createNewUsersFolder(User user) {
        String user_folder_path = user.getUser_folder_path();
        if (user_folder_path == null) {
            return false;
        }

        File file = new File(user_folder_path);
        return file.mkdir();
    }

//    private void startNewClientHandler(User user) throws IOException {
//        Thread thread = new Thread(new ClientHandler(socket, user));
//        thread.setDaemon(true);
//        thread.start();
//    }


}

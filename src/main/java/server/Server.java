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

    public void run() throws IOException {

        ServerSocket server = new ServerSocket(8189);
        log.info("Server started...");

        while (true) {
            try {
                this.socket = server.accept();
                log.info("Server connect...");

                Thread thread = new Thread(new ClientHandler(socket));
                thread.setDaemon(true);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

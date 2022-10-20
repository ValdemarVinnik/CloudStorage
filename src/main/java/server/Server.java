package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(8189);
        System.out.println("Server started...");

        while (true){
            try {
                Socket socket = server.accept();
                System.out.println("Server connected with client...");

                new Thread(new ClientHandler(socket)).start();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

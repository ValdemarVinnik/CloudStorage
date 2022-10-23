package client;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    private final int CONNECTION_TIMEOUT = 5;
    private final Controller controller;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream ous;



    public Client(Controller controller) {
        this.controller = controller;
    }

    public void openConnection() throws IOException {
        socket = new Socket("localhost", 8189);
        is = new DataInputStream(socket.getInputStream());
        ous = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                if (waitConnection()) {
                    readMessage();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Почему то закрылось соединение");
                closeConnection();
            }
        }).start();
    }

    private void readMessage() throws IOException {
        String message;
        int count =0;
        try {
            while (true) {
                message = is.readUTF();
                System.out.println(++count);
                System.out.println("мы здесь" + message);
                controller.addMessage(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean waitConnection() throws IOException {
        int count = 0;
        try {
            while (count < CONNECTION_TIMEOUT) {
                if (is.readUTF().equals("start")) {
                    System.out.println("start");
                    return true;
                }
                Thread.sleep(1000);
                count++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void closeConnection() {

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (ous != null) {
            try {
                ous.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().exit(0);
    }

    private void displayFolder(String...file){
        Platform.runLater(() -> controller.displayUsersListView(file));
    }

    public void downloadFile(File selectedFile) {
        System.out.println(selectedFile.getName());
        controller.addMessage(selectedFile.getName());
    }
}

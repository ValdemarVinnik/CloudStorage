package client;

import common.Command;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    private final int CONNECTION_TIMEOUT = 5;
    private final byte[] BUFFER = new byte[8119];
    private static String currentPathOnTheServer = "/???";
    private static final File CLIENT_ROOT = new File("src/main/java/client/root");

    private static String currentPath = CLIENT_ROOT.getAbsolutePath();
    private File selectedFile;
    private String[] contentCurrentDirectory;
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
        int count = 0;
        try {
            while (true) {
                message = is.readUTF();

                if (message.equals(Command.LOCATION.getCommand())) {
                    acceptLocation();
                }

                if (message.equals(Command.DIR_CONTENT.getCommand())){
                    acceptDirContent();
                }

                controller.addMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void acceptDirContent() throws IOException {
        contentCurrentDirectory = is.readUTF().split("/");
       controller.displayServerListView(contentCurrentDirectory);// как то криво
    }

    public String[] getContentCurrentDirectory() {
        if (contentCurrentDirectory == null) {
            return new String[]{"???"};
        }
        return contentCurrentDirectory;
    }

    private void acceptLocation() throws IOException {
        currentPathOnTheServer = is.readUTF();

        controller.displayServerCurrentPath(currentPathOnTheServer);// как то криво
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public String getCurrentPathOnTheServer() {
        return currentPathOnTheServer;
    }


    private boolean waitConnection() throws IOException {
        int count = 0;
        try {
            while (count < CONNECTION_TIMEOUT) {
                if (is.readUTF().equals(Command.START.getCommand())) {
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

    public void unloadFile(File selectedFile) throws IOException {

        writeUTF("#file");
        writeUTF(selectedFile.getName());
        writeSize(selectedFile.length());

        try (FileInputStream fis = new FileInputStream(selectedFile)) {
            int read;
            while ((read = fis.read(BUFFER)) != -1) {
                writeBytes(BUFFER, 0, read);
            }
        }
    }

    private void writeUTF(String message) throws IOException {
        ous.writeUTF(message);
        ous.flush();
    }

    private void writeBytes(byte[] buffer, int off, int length) throws IOException {
        ous.write(buffer, off, length);
        ous.flush();
    }

    private void writeSize(Long size) throws IOException {
        ous.writeLong(size);
        ous.flush();
    }
}

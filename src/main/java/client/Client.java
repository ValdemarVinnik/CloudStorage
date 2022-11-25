package client;

import client.controllers.Controller;
import common.Command;
import common.model.User;
import lombok.extern.slf4j.Slf4j;


import java.io.*;
import java.net.Socket;


@Slf4j
public class Client {
    private static Client client;
    private final int CONNECTION_TIMEOUT = 4;
    private final int SIZE = 8192;
    private final byte[] BUFFER = new byte[SIZE];
    private static String currentPathOnTheServer = "disconnect";

    private static final File CLIENT_ROOT = new File("src/main/java/client/root");
    private File currentUserDirectory;
    private static String currentPath = CLIENT_ROOT.getAbsolutePath();
    private File selectedUserFile;

    private String selectedServerFileName;
    private String[] contentCurrentServersDirectory;
    private Controller controller;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream ous;


    private Client() {

        this.currentUserDirectory = CLIENT_ROOT;
    }

    public static Client getInstance() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public void setController(Controller controller) {
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
                log.debug(message);

                if (message.equals(Command.LOCATION.getCommand())) {
                    acceptLocation();
                }

                if (message.equals(Command.DIR_CONTENT.getCommand())) {
                    acceptDirContent();
                }

                if (message.equals(Command.SEND.getCommand())) {
                    readFile();
                    controller.displayUsersListView(currentUserDirectory.list());
                }

//                controller.addMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void acceptDirContent() throws IOException {
        contentCurrentServersDirectory = is.readUTF().split("/");
        controller.displayServerListView(contentCurrentServersDirectory);// как то криво
    }

    public String[] getContentCurrentServersDirectory() {
        if (contentCurrentServersDirectory == null) {
            return new String[]{"???"};
        }
        return contentCurrentServersDirectory;
    }

    private void acceptLocation() throws IOException {
        currentPathOnTheServer = is.readUTF();
        log.debug(currentPathOnTheServer);

        controller.displayServerCurrentPath(currentPathOnTheServer);// как то криво
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public File getSelectedUserFile() {
        return selectedUserFile;
    }

    public String getCurrentPathOnTheServer() {
        return currentPathOnTheServer;
    }


    private boolean waitConnection() throws IOException {
        int count = 0;
        try {
            while (count < CONNECTION_TIMEOUT) {
                if (is.readUTF().equals(Command.START.getCommand())) {
                    log.debug("Connection Ok");
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


    public void sendSelectedFile() throws IOException {
        if (selectedUserFile != null && !selectedUserFile.isDirectory()) {

            writeUTF(Command.SEND.getCommand());
            writeUTF(selectedUserFile.getName());
            writeSize(selectedUserFile.length());

            try (FileInputStream fis = new FileInputStream(selectedUserFile)) {
                int read;
                while ((read = fis.read(BUFFER)) != -1) {
                    writeBytes(BUFFER, 0, read);
                }
                log.debug(String.format("File %s was unloaded", selectedUserFile.getName()));
            }
        }
    }

    private void readFile() throws IOException {
        log.debug("readFile");
        String fileName = is.readUTF(); // нужно будет добавить поп-ап окно с предупреждениеи, если такой файл существует
        File file = new File(currentUserDirectory.getAbsolutePath() + "/" + fileName);
        long size = is.readLong();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (int i = 0; i < (size + SIZE - 1) / SIZE; i++) {

                int read = is.read(BUFFER);
                fos.write(BUFFER, 0, read);
            }
            ous.writeUTF(fileName + " is unloaded");
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

    public void riseUp() {
        selectedUserFile = selectedUserFile.getParentFile();
        currentPath = selectedUserFile.getAbsolutePath();
    }

    public void GoDown(String selectedFileName) {
        currentPath = currentPath + "\\" + selectedFileName;
        controller.displayUserCurrentPath(currentPath);
    }

    public void createSelectedFile() {
        selectedUserFile = new File(currentPath);
        if (selectedUserFile.isDirectory()) {
            currentUserDirectory = selectedUserFile;
        }
    }

    public void goDownServerPath(String selectedServerFileName) throws IOException {

        writeUTF(Command.DAWN.getCommand());
        writeUTF(selectedServerFileName);
    }

    public void goUpServerPath() {
        try {
            writeUTF(Command.UP.getCommand());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDownloadRequest() throws IOException {
        writeUTF(Command.DOWNLOAD_REQUEST.getCommand());

    }

    public void renameFileOnServer(String oldFileName, String newFileName) {
        try {
            writeUTF(Command.RENAME.getCommand());
            writeUTF(oldFileName);
            writeUTF(newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileOnServer(String fileNameForDelete) {
        try {
            writeUTF(Command.DELETE.getCommand());
            writeUTF(fileNameForDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNewFolderOnServer(String folderName) {
        try {
            writeUTF(Command.FOLDER.getCommand());
            writeUTF(folderName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(User user) {
        try {
            writeUTF(Command.REG.getCommand());
            new ObjectOutputStream(socket.getOutputStream()).writeObject(user);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package server;

import common.*;
import common.model.User;
import lombok.extern.slf4j.Slf4j;
import server.db.DBConnection;

import java.io.*;
import java.net.Socket;

@Slf4j
public class ClientHandler implements Runnable {
    private User user;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream ous;
    private DBConnection dbConnection;
    private final int SIZE = 8192;
    private final byte[] BUFFER = new byte[SIZE];
    private final String SERVER_ROOT = "src/main/java/server/root";
    private String currentPath;
    private File currentFile;
    private String[] currentDirectoryContent;
    private File currentDirectory;

    private boolean running = false;

    public ClientHandler(Socket socket) throws IOException {
        dbConnection = DBConnection.getInstance();
        // this.user = user;
        this.socket = socket;
        running = true;
        is = new DataInputStream(socket.getInputStream());
        ous = new DataOutputStream(socket.getOutputStream());
        // ous.writeUTF(Command.START.getCommand());
        // ois = new ObjectInputStream(socket.getInputStream());
        identifyUser();
        //currentFile = new File(user.getUser_folder_path());
        updateCurrentPath();
        currentDirectoryContent = currentFile.list();
        currentDirectory = currentFile;
    }

    private void identifyUser() throws IOException {
        try {


            //ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            String massage = is.readUTF();

            if (massage.equals(Command.REG.getCommand())) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                this.user = (User) ois.readObject();
                dbConnection.registerUser(user);
                createNewUsersFolder(user);
                // ous. error

            }

            if (massage.equals(Command.AUTH.getCommand())) {
                // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // User user = (User) ois.readObject();
//                if (dbConnection.getUserByLoginAndPassword(user.getLogin(), user.getPassword()) == null) {
//                    // startNewClientHandler(user);
//                }

            }

        } catch (IOException | ClassNotFoundException e) {
            log.error(e.toString());
        }
    }

    private boolean createNewUsersFolder(User user) {
        String user_folder_path = dbConnection.getUserByLoginAndPassword(user);
        if (user_folder_path == null) {
            return false;
        }

        currentFile = new File(user_folder_path);
        return currentFile.mkdir();
    }

    private void stopHandler() {
        running = false;
    }

    @Override
    public void run() {


        try {
            ous.writeUTF(Command.START.getCommand());
            ous.writeUTF("Open connection...");
            sendCurrentLocation();
            sendCurrentDirectoryContent();

            while (running) {
                String clientMessage = is.readUTF();
                log.debug("Command is : " + clientMessage);

                if (clientMessage.equals(Command.SEND.getCommand())) {
                    readFile();
                    updateCurrentDirectory();
                    sendCurrentDirectoryContent();
                }

                if (clientMessage.equals(Command.DAWN.getCommand())) {
                    goDawn();
                    sendCurrentLocation();
                    sendCurrentDirectoryContent();
                }

                if (clientMessage.equals(Command.UP.getCommand())) {
                    goUP();
                    sendCurrentLocation();
                    sendCurrentDirectoryContent();
                }

                if (clientMessage.equals(Command.DOWNLOAD_REQUEST.getCommand())) {
                    sendFile();
                }

                if (clientMessage.equals(Command.RENAME.getCommand())) {
                    renameFile();
                    updateCurrentPath();
                    sendCurrentLocation();
                    sendCurrentDirectoryContent();
                }


                if (clientMessage.equals(Command.DELETE.getCommand())) {
                    deleteFile();
                    updateCurrentPath();
                    updateCurrentDirectory();
                    sendCurrentLocation();
                    sendCurrentDirectoryContent();
                }

                if (clientMessage.equals(Command.FOLDER.getCommand())) {
                    createFolder();
                    updateCurrentPath();
                    updateCurrentDirectory();
                    sendCurrentLocation();
                    sendCurrentDirectoryContent();
                }


                if (clientMessage.equals("end")) {
                    stopHandler();
                    ous.writeUTF("server disconnected.");
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerUser() throws IOException {
        try {
            User user = (User) new ObjectInputStream(socket.getInputStream()).readObject();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createFolder() throws IOException {
        String newFolderName = is.readUTF();
        File file = new File(currentPath + "/" + newFolderName);
        file.mkdir();
        log.debug("file " + newFolderName + " is directory " + file.isDirectory());
    }

    private void deleteFile() throws IOException {
        String fileName = is.readUTF();
        File fileForDelete = new File(currentPath + "/" + fileName);
        if (!fileForDelete.delete()) {
            log.debug("File " + fileName + " non deleted");
        }
        ;
    }

    private void renameFile() throws IOException {

        String oldFileName = is.readUTF();
        String newFileName = is.readUTF();
        log.debug(newFileName);

        File oldFile = new File(currentPath + "/" + oldFileName);
        log.debug(oldFileName + " is exist - " + oldFile.exists());

        File newFile = new File(currentPath + "/" + newFileName);
        log.debug(newFileName + " is exist - " + newFile.exists());

        boolean successful = oldFile.renameTo(newFile);
        log.debug("Is rename successful - " + successful);

        currentFile = newFile;
    }

    private void writeSize(Long size) throws IOException {
        ous.writeLong(size);
        ous.flush();
    }

    private void writeUTF(String message) throws IOException {
        ous.writeUTF(message);
        ous.flush();
    }

    private void writeBytes(byte[] buffer, int off, int length) throws IOException {
        ous.write(buffer, off, length);
        ous.flush();
    }


    private void updateCurrentPath() {
        currentPath = currentFile.getAbsolutePath();
    }

    private void updateCurrentDirectory() {
        currentDirectory = currentFile;
        currentDirectoryContent = currentFile.list();
    }

    private void goUP() {
        if (!currentFile.getParentFile().getAbsolutePath().equals(SERVER_ROOT)) {
            currentFile = currentFile.getParentFile();
            updateCurrentDirectory();
            updateCurrentPath();
        }
    }

    private void goDawn() throws IOException {// выбираем файл
        String currentFileName = is.readUTF();
        log.debug(currentFileName);
        currentFile = new File(currentPath + "/" + currentFileName);

        log.debug(currentFile.getAbsolutePath());

        if (currentFile.isDirectory()) {
            updateCurrentPath();
            updateCurrentDirectory();
        }

    }

    private void sendCurrentDirectoryContent() throws IOException {
        ous.writeUTF(Command.DIR_CONTENT.getCommand());

        StringBuilder content = new StringBuilder();

        for (String each : currentDirectoryContent) {
            content.append(each + "/");
        }
        ous.writeUTF(content.toString());
    }

    private void sendCurrentLocation() throws IOException {
        ous.writeUTF(Command.LOCATION.getCommand());
        ous.writeUTF(currentFile.getAbsolutePath().replace(SERVER_ROOT, ""));

    }

    private void readFile() throws IOException {
        log.debug("readFile");
        String fileName = is.readUTF();
        File file = new File(currentDirectory.getAbsolutePath() + "/" + fileName);
        long size = is.readLong();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (int i = 0; i < (size + SIZE - 1) / SIZE; i++) {

                int read = is.read(BUFFER);
                fos.write(BUFFER, 0, read);
            }
            ous.writeUTF(fileName + " is unloaded");
        }
    }

    private void sendFile() throws IOException {
        if (currentFile != null && !currentFile.isDirectory()) {

            writeUTF(Command.SEND.getCommand());
            writeUTF(currentFile.getName());
            writeSize(currentFile.length());

            try (FileInputStream fis = new FileInputStream(currentFile)) {
                int read;
                while ((read = fis.read(BUFFER)) != -1) {
                    writeBytes(BUFFER, 0, read);
                }
                log.debug(String.format("File %s was unloaded", currentFile.getName()));
            }
        }
    }
}


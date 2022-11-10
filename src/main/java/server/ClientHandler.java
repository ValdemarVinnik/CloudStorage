package server;

import common.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.List;

@Slf4j
public class ClientHandler implements Runnable {

    private DataInputStream is;
    private DataOutputStream ous;
    private final int SIZE = 8192;
    private final byte[] BUFFER = new byte[SIZE];
    private final String SERVER_ROOT = "src/main/java/server/root";
    private String currentPath = SERVER_ROOT;
    private File currentFile = new File(SERVER_ROOT);
    private String[] currentDirectoryContent;
    private File currentDirectory;

    private boolean running = false;

    public ClientHandler(Socket socket) throws IOException {
        running = true;
        is = new DataInputStream(socket.getInputStream());
        ous = new DataOutputStream(socket.getOutputStream());
        currentDirectoryContent = currentFile.list();
        currentDirectory = currentFile;
    }

    private void stopHandler() {
        running = false;
    }

    @Override
    public void run() {

        try {
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

                if (clientMessage.equals(Command.RENAME.getCommand())){
                    renameFile();
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

    private void renameFile() throws IOException {

        String oldFileName = is.readUTF();
        String newFileName = is.readUTF();
        log.debug(newFileName);

        File oldFile = new File(currentPath + "/"+ oldFileName);
        log.debug(oldFileName +" is exist - " + oldFile.exists());

        File newFile = new File(currentPath+ "/" +newFileName);
        log.debug(newFileName +" is exist - " + newFile.exists());

        boolean successful = oldFile.renameTo(newFile);
        log.debug("Is rename successful - "+ successful);

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
        currentFile = currentFile.getParentFile();
        updateCurrentDirectory();
        updateCurrentPath();
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
        ous.writeUTF(currentFile.getAbsolutePath());

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


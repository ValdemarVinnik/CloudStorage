package server;

import common.*;

import java.io.*;
import java.net.Socket;
import java.util.List;


public class ClientHandler implements Runnable {

    private DataInputStream is;
    private DataOutputStream ous;
    private final int SIZE = 8192;
    private final byte[] BUFFER = new byte[SIZE];
    private final String SERVER_ROOT = "src/main/java/server/root/";
    private File currentFile = new File(SERVER_ROOT);
    private String[] currentDirectoryContent;

    private boolean running = false;

    public ClientHandler(Socket socket) throws IOException {
        running = true;
        is = new DataInputStream(socket.getInputStream());
        ous = new DataOutputStream(socket.getOutputStream());
        currentDirectoryContent = currentFile.list();
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
                if (clientMessage.equals("#file")) {
                    readFile();
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

    private void sendCurrentDirectoryContent() throws IOException {
        ous.writeUTF(Command.DIR_CONTENT.getCommand());

        StringBuilder content = new StringBuilder();

        for (String each : currentDirectoryContent){
            content.append(each+"/");
        }
            ous.writeUTF(content.toString());
    }

    private void sendCurrentLocation() throws IOException {
        ous.writeUTF(Command.LOCATION.getCommand());
        ous.writeUTF(currentFile.getAbsolutePath());

    }

    private void readFile() throws IOException {
        String fileName = is.readUTF();
        File file = new File(SERVER_ROOT + fileName);
        long size = is.readLong();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (int i = 0; i < (size + SIZE - 1) / SIZE; i++) {

                int read = is.read(BUFFER);
                fos.write(BUFFER, 0, read);
            }
            ous.writeUTF(fileName + " is unloaded");
        }
    }
}

package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

  private DataInputStream is;
  private DataOutputStream ous;

  private boolean running = false;

  public ClientHandler(Socket socket) throws IOException {
    running = true;
    is = new DataInputStream(socket.getInputStream());
    ous = new DataOutputStream(socket.getOutputStream());
  }

  private void stopHandler(){
    running = false;
  }

  @Override
    public void run() {

    try {
      ous.writeUTF("Open connection...");

      while (running) {
        String clientMessage = is.readUTF();
        if (clientMessage.equals("end")){
          stopHandler();
          ous.writeUTF("server disconnected.");
          break;
        }
        System.out.println("Received: "+ clientMessage);
        ous.writeUTF(clientMessage);
      }

    }catch (Exception e){
      e.printStackTrace();
    }
    }
}

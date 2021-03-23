package server;

import lib.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Connection implements Runnable{
  private Model model;
  private Socket socket;
  private User user;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private List<String> userList;
  private boolean closed = false;

  public Connection(Model model, Socket socket, List<String> userList) {
    this.model = model;
    this.socket = socket;
    this.userList = userList;
    try {
      this.inputStream = new ObjectInputStream(socket.getInputStream());
      this.outputStream = new ObjectOutputStream(socket.getOutputStream());
      this.closed = false;
      System.out.println("Inizialized connection");
    } catch (IOException e){
      e.printStackTrace();
      // TODO invalid socket
    }
  }

  @Override
  public void run() {
    String command = null;
    Object o = null;
    System.out.println("Eseguendo comando");
    try {
      if ((o = inputStream.readObject()) != null) {
        if (o instanceof User) {
          if (verifyUser((User) o)) {
            outputStream.writeObject(true);
            handleCall();
          } else {
            outputStream.writeObject(false);
            closeConnection();
          }
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    //Do your logic here. You have the `socket` available to read/write data.
    handleCall();
    //Make sure to close
    try {
      socket.close();
    }catch(IOException ioe) {
      System.out.println("Error closing client connection");
    }
  }

  private boolean verifyUser(User user) {
    return this.userList.contains(user.getUserName());
  }

  private void closeConnection() {
    if (!closed) {
      System.out.println(socket);
      try {
        inputStream.close();
        outputStream.close();
        socket.close();
        this.user = null;
        this.model = null;
        this.closed = true;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void handleCall() {
    System.out.println("Command done");
    closeConnection();
  }

  /*private String handleCall() {
    if (!closed) {
      try {
        Object o;
        if ((o = inputStream.readObject()) != null) {
          if (o != null && o instanceof String) {
            String command = (String) o;

            switch (command) {
              case "login":
                login();
                break;
              default:
                break;
            }
          }
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }*/

  boolean isClosed() {
    return closed;
  }
}

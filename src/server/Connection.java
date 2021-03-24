package server;

import lib.User;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Connection implements Runnable{
  private PrintStream ps;
  private Model model;
  private Socket socket;
  private User user;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private List<String> userList;
  private boolean closed = false;

  public Connection(Model model, Socket socket, List<String> userList, PrintStream ps) {
    this.ps = ps;
    this.model = model;
    this.socket = socket;
    this.userList = userList;
    try {
      this.inputStream = new ObjectInputStream(socket.getInputStream());
      this.outputStream = new ObjectOutputStream(socket.getOutputStream());
      this.closed = false;
    } catch (IOException e){
      e.printStackTrace();
      // TODO invalid socket
    }
  }

  @Override
  public void run() {
    try {
      String command = null;
      Object o = null;
      if (!closed) {
        try {
          if ((o = inputStream.readObject()) != null) {
            if (o instanceof User) {
              if (verifyUser((User) o)) {
                this.model.addUser((User) o);
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
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null && outputStream != null) {
        if (!closed)
          closeConnection();
      }
    }
  }

  private boolean verifyUser(User user) {
    return this.userList.contains(user.getUserName());
  }

  private void closeConnection() {
    if (!closed) {
      try {
        if (this.user != null) {
          ps.println(this.user.getUserName() + "closed connection");
        }
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

  private void handleCall() throws IOException, ClassNotFoundException {
    String command = null;
    if (!closed) {
      Object o;
      try {
        if ((o = inputStream.readObject()) != null) {
          if (o instanceof String) {
            command = (String) o;
            switch (command) {
              case "close_connection":
                freeUser();
                closeConnection();
                break;
              default:
                break;
            }
          }
        }
      } catch (EOFException e) {
        //do nothing
      }
    }
    closeConnection();
  }

  private void freeUser() {
    if (!closed) {
      this.model.freeUser(this.user);
    }
  }

  boolean isClosed() {
    return closed;
  }
}

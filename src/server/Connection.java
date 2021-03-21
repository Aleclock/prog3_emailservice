package server;

import lib.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Connection implements Runnable{
  private Model model;
  private Socket socket;
  private User user;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private boolean closed = false;

  public Connection(Model model, Socket socket) {
    this.model = model;
    this.socket = socket;
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
      Object o = null;
      if (!closed) {
        try {
          socket.setSoTimeout(5);
          if ((o = inputStream.readObject()) != null) {

          }
        } catch(SocketTimeoutException | EOFException ex) {
          ex.printStackTrace();
        }
      }
    } catch(SocketException e) {
      e.printStackTrace();
    } catch(IOException | ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null && outputStream != null) {
        try {
          if (!closed)
            this.closeConnection();
        } catch (NullPointerException e){
          e.printStackTrace();
        }
      }
    }
  }

  private void closeConnection() {
    if (!closed) {
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

  boolean isClosed() {
    return closed;
  }
}

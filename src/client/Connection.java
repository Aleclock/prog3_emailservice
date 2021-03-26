package client;

import lib.Email;
import lib.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Connection {
  private Socket socket;
  private User user;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private boolean isConnected = false;

  public Connection() {}

  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Open server connection (socket, port 8189), create object input/output stream
   */
  public void connect(){
    try {
      this.socket = new Socket(InetAddress.getLocalHost().getHostName(), 8189);
      if (socket != null) {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.isConnected = true;
      }
    } catch (IOException e){
      this.isConnected = false;
    }
  }

  /**
   * Close connection with server
   */
  public void close() {
    if (this.socket != null) {
      try {
        this.outputStream.writeObject("close_connection");
        this.outputStream.close();
        this.inputStream.close();
        this.socket.close();
      } catch (IOException e) {
        this.isConnected = false;
        e.printStackTrace();
      }
    }
  }

  /**
   * Send to server User object and retrieve true/false if user's mail exists or not
   */
  public boolean login(String email){
    if (outputStream != null) {
      try {
        outputStream.writeObject(new User(email));
        Object o = inputStream.readObject();
        if (o != null && o instanceof Boolean) {
          return (boolean) o;
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  public boolean isConnected() {
    return this.isConnected;
  }
}

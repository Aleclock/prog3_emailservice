package client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lib.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection {
  private Socket socket;
  private User user;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  final private BooleanProperty connectionStatus = new SimpleBooleanProperty(false);
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
      if (socket.isConnected()) {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.isConnected = true;
        setConnectionStatus(true);
      }
    } catch (IOException e){
      this.isConnected = false;
      setConnectionStatus(false);
    }
  }

  /**
   * Close connection with server
   */

  public void closeConnection() {
    try {
      this.outputStream.close();
      this.inputStream.close();
      this.socket.close();
    } catch (IOException e) {
      this.isConnected = false;
      e.printStackTrace();
    }
  }

  /**
   * Logout
   */
  public void logout() {
    if (isConnected()) {
      try {
        this.outputStream.writeObject(new Command(this.user, "logout", null));
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
  public OperationResponse login(String email){
    OperationResponse result = new OperationResponse(false, "");
    if (isConnected()) {
      try {
        this.outputStream.writeObject(new Command(new User(email), "login", null));
        Object o = this.inputStream.readObject();
        if (o.getClass() == OperationResponse.class) {
          return (OperationResponse) o;
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * il discorso è che ogni volta viene creata una nuova connessione, quindi è necessario inviare prima l'utente e
   * poi l'azione da compiere, forse ha più senso inviare una coppia <User, String> dove String è l'azione da compiere
   * quindi login, closeConnection, readEmails. in questo modo non è necessario inviare troppe cose
   */
  public OperationResponse getEmails() {
    OperationResponse result = new OperationResponse(false, "");
    if (isConnected()) {
      try {
        this.outputStream.writeObject(new Command(this.user, "read_emails", null));
        Object o = this.inputStream.readObject();
        if (o.getClass() == OperationResponse.class) {
          result = (OperationResponse) o;
          //EmailBox emailBox = ((OperationResponse) o).getEmailBox();
          //emails = emailBox.getEmailList();
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public OperationResponse sendEmail(Email email) {
    OperationResponse result = new OperationResponse(false, "");
    if (isConnected()) {
      try {
        Command command = new Command(this.user, "send_email", email);
        this.outputStream.writeObject(command);
        Object o = inputStream.readObject();
        if (o.getClass() == OperationResponse.class) {
          String message;
          if (((OperationResponse) o).getResult()) {
            message = LabelMessage.emailSentSuccess;
            result.setEmail(((OperationResponse) o).getEmail());
          } else {
            message = LabelMessage.emailSentError;
          }
          result.set(((OperationResponse) o).getResult(), message);
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public OperationResponse deleteEmail (Email email) {
    OperationResponse result = new OperationResponse(false, "");
    if (isConnected()) {
      try {
        Command command = new Command(this.user, "delete_email", email);
        this.outputStream.writeObject(command);
        Object o = inputStream.readObject();
        if (o.getClass() == OperationResponse.class) {
          String message;
          if (((OperationResponse) o).getResult()) {
            message = LabelMessage.emailDeleteSuccess;
          } else {
            message = LabelMessage.emailDeleteError;
          }
          result.set(((OperationResponse) o).getResult(), message);
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public OperationResponse setRead(Email email, boolean read) {
    OperationResponse result = new OperationResponse(false, "");
    if (isConnected()) {
      try {
        String code;
        if (read) {
          code = "set_email_read";
        } else {
          code = "set_email_unread";
        }
        Command command = new Command(this.user, code, email);
        this.outputStream.writeObject(command);
        Object o = inputStream.readObject();
        if (o.getClass() == OperationResponse.class) {
          result = (OperationResponse) o;
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public boolean isConnected() {
    return this.isConnected;
  }

  public BooleanProperty getConnectionStatus() {
    return this.connectionStatus;
  }

  public void setConnectionStatus(boolean value) {
    this.connectionStatus.setValue(value);
  }
}

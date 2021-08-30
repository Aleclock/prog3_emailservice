package client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lib.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Connection {
  private Socket socket;
  private User user;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private BooleanProperty connectionStatus = new SimpleBooleanProperty(false);
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
        this.connectionStatus.setValue(true);
      }
    } catch (IOException e){
      this.isConnected = false;
      this.connectionStatus.setValue(false);
    }
  }

  /**
   * Close connection with server
   */
  public void close() {
    if (this.outputStream != null) {
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
  public boolean login(String email){
    if (this.outputStream != null) {
      try {
        this.outputStream.writeObject(new Command(new User(email), "login", null));
        Object o = this.inputStream.readObject();
        if (o instanceof Boolean) {
          return (boolean) o;
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  public List<Email> getEmails() {
    List<Email> emails = null;
    if (this.outputStream != null) {
      try {
        /**
         * il discorso è che ogni volta viene creata una nuova connessione, quindi è necessario inviare prima l'utente e
         * poi l'azione da compiere, forse ha più senso inviare una coppia <User, String> dove String è l'azione da compiere
         * quindi login, closeConnection, readEmails. in questo modo non è necessario inviare troppe cose
         */
        this.outputStream.writeObject(new Command(this.user, "read_emails", null));
        Object o = this.inputStream.readObject();
        if (o instanceof EmailBox) {
          EmailBox emailBox = (EmailBox) o;
          emails = emailBox.getEmailList();
        } else {
          // TODO lettura email box fallita
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return emails;
  }

  public String sendEmail(Email email) {
    String message = "";
    if (this.outputStream != null) {
      try {
        Command command = new Command(this.user, "send_email", email);
        this.outputStream.writeObject(command);
        Object o = inputStream.readObject();
        if (o instanceof Boolean) {
          if ((Boolean) o) {
            message = LabelMessage.emailSentSuccess;
          } else {
            message = LabelMessage.emailSentError;
          }
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return message;
  }

  public String deleteEmail (Email email) {
    String message = "";
    if (this.outputStream != null) {
      try {
        Command command = new Command(this.user, "delete_email", email);
        this.outputStream.writeObject(command);
        Object o = inputStream.readObject();
        if (o instanceof Boolean) {
          if ((Boolean) o) {
            message = LabelMessage.emailDeleteSuccess;
          } else {
            message = LabelMessage.emailDeleteError;
          }
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return message;
  }

  public boolean setRead(Email email, boolean read) {
    boolean result = false;
    if (this.outputStream != null) {
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
        if (o instanceof Boolean) {
          result = (Boolean) o;
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

  // TODO ha senso usarlo per qualche operazione?
  public void setConnectionStatus(boolean value) {
    this.connectionStatus.setValue(value);
  }
}

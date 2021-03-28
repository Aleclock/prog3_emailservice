package client;

import lib.Command;
import lib.Email;
import lib.EmailBox;
import lib.User;

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
    if (this.outputStream != null) {
      try {
        //this.outputStream.writeObject(new User(email));
        // TODO se faccio il login forse dopo devo settare this.user
        this.outputStream.writeObject(new Command(new User(email), "login", null));
        Object o = this.inputStream.readObject();
        if (o != null && o instanceof Boolean) {
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
        if (o != null && o instanceof EmailBox) {
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

  public String sendEmail(User user, List<User> recipients, String subject, String body) {
    String message = "";
    if (this.outputStream != null) {
      try {
        Email email = new Email(user, recipients, subject, body);
        Command command = new Command(this.user, "send_email", email);
        this.outputStream.writeObject(command);
        Object o=inputStream.readObject();
        if (o != null && o instanceof Boolean) {
          if ((Boolean) o) {
            message = "Invio email: Email sent correctly";
          } else {
            message = "Failure";
          }
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    return message;
  }

  public boolean isConnected() {
    return this.isConnected;
  }
}

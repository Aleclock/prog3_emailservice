package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lib.Email;
import lib.User;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Model {
  private Connection connection;
  private User user;
  private final ObservableList<Email> mails;


  public Model() {
    this.connection = new Connection();
    this.mails = FXCollections.observableList(new ArrayList<Email>());
  }

  public void connectUser() throws SocketException {
    this.connection.connect();
    if (!this.connection.isConnected()) {
      throw new SocketException("Impossibile raggiungere il server");
    }
  }

  public String requestSendMail(String recipients, String subject, String body) throws IOException {
    String message = ""; // TODO capire se ha senso
    List<User> recipientsList = stringToUserList(recipients);

    if (recipientsList.isEmpty()) {
      // TODO inserire almeno un destinatario
      // TODO valutare se segnalare anche che non c'è l'oggetto o il testo del messaggio
    } else {
      connectUser();
      message = this.connection.sendEmail(this.user, recipientsList, subject, body);
    }
    return message;
  }

  public void retrieveEmails() {
    try {
      connectUser();
      List<Email> newEmail = this.connection.getEmails();
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public Connection getConnection() {
    return connection;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getUser() {
    return this.user;
  }

  public void closeConnection() {
    if (this.connection != null) {
      connection.close();
      System.out.println("Connessione chiusa");
    }
  }

  // String... stringUtenti
  // in questo modo sarebbe possibile passare una o più stringhe, non so se possa servire
  private List<User> stringToUserList(String string) {
    List<User> userList = new ArrayList<>();
    String[] userNames = string.split("\\s+");
    for (String email: userNames) {
      userList.add(new User(email));
    }
    return userList;
  }

}

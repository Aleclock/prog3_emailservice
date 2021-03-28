package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lib.Email;
import lib.User;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Model {
  private Connection connection;
  private User user;
  private final ObservableList<Email> emails;


  public Model() {
    this.connection = new Connection();
    this.emails = FXCollections.observableList(new ArrayList<Email>());
  }

  public void connectUser() throws SocketException {
    this.connection.connect();
    if (!this.connection.isConnected()) {
      throw new SocketException("Impossibile raggiungere il server");
    }
  }

  /**
   * Create a thread that call retrieveEmails method every 5000 seconds, in order to update client emails list
   */
  public void refreshEmailList() {
    Thread emailRefresh = new Thread(new Runnable() {
      @Override
      public void run() {
        // TODO capire se farlo dipendere dallo stato della connessione
        while (true) {
          try {
            sleep(5000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Platform.runLater(() -> {
            retrieveEmails();
          });
        }
      }
    });
    //emailRefresh.setDaemon(true); // TODO capire a cosa serve
    emailRefresh.start();
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
      System.out.println(message);
    }
    return message;
  }

  /*
  synchronized in modo tale che se più thread tentano di eseguire questo metodo, non lo fanno in contemporanea. Se
  il metodo è disponibile, il primo thread può accedere, mentre se è occupato il thread viene messo in coda fino a quando
  non è disponibile
   */
  synchronized public void retrieveEmails() {
    try {
      connectUser();
      List<Email> newEmail = this.connection.getEmails();
      System.out.println(newEmail);
      synchronized (this.emails) {
        if (!newEmail.isEmpty()) {
          newEmail.removeAll(this.emails);
          System.out.println(newEmail);
          this.emails.addAll(newEmail);
        }
      }
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

  public ObservableList<Email> getEmails() {
    retrieveEmails();
    return this.emails;
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

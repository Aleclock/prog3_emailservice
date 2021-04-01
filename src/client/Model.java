package client;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lib.Email;
import lib.EmailProperty;
import lib.User;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class Model {
  private Connection connection;
  private User user;
  private final ObservableList<Email> emails;
  private final ObservableList<Email> emailsSent;
  private final ObservableList<Email> emailReceived;
  private SimpleObjectProperty<EmailProperty> currentEmailSelected = new SimpleObjectProperty<>(null);


  public Model() {
    this.connection = new Connection();
    this.emails = FXCollections.observableList(new ArrayList<>());
    this.emailsSent = FXCollections.observableList(new ArrayList<>());
    this.emailReceived = FXCollections.observableList(new ArrayList<>());
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
    //emailRefresh.setDaemon(true); // TODO capire a cosa serve setDeamon
    emailRefresh.start();
  }

  public String requestSendMail(String recipients, String subject, String body) throws IOException {
    String message = "";
    List<User> recipientsList = stringToUserList(recipients);

    if (recipientsList.isEmpty()) {
      // TODO inserire almeno un destinatario
      // TODO valutare se segnalare anche che non c'è l'oggetto o il testo del messaggio
    } else {
      connectUser();
      Email email = new Email(this.user, recipientsList, subject, body);
      message = this.connection.sendEmail(email);
      // TODO cambiare messaggio, non corretto che fa schifo
      if (message.contains("corretto")) {
        email.setRead(true);
        this.emailsSent.add(0, email);
      }
    }
    return message;
  }

  public String requestDeleteEmail (long uuid) throws IOException{
    String message = "";

    connectUser();
    Email email = getEmailByUUID(uuid);
    if (email != null) {
      message = this.connection.deleteEmail(email);
      if (message.contains("correctly")) {
        // TODO delete email from lists (può essere sia inviata che ricevuta, stare attento)
      }
    }
    return message;
  }

  /*
  synchronized in modo tale che se più thread tentano di eseguire questo metodo, non lo fanno in contemporanea. Se
  il metodo è disponibile, il primo thread può accedere, mentre se è occupato il thread viene messo in coda fino a quando
  non è disponibile
   */
  // TODO ha senso usare due synchronized???
  synchronized public void retrieveEmails() {
    try {
      connectUser();
      List<Email> newEmail = this.connection.getEmails();
      // TODO da consegna dovrebbe creare un popup nel caso di nuove mail, in pratica le aggiunge semplicemente in testa

      if (this.emailsSent.isEmpty()) {
        List<Email> send = newEmail.stream().filter(e -> e.getSender().equals(this.user)).collect(Collectors.toList());
        this.emailsSent.addAll(send);
      }

      synchronized (this.emails) {
        if (!newEmail.isEmpty()) {
          List<Email> received = newEmail.stream().filter(e -> !e.getSender().equals(this.user)).collect(Collectors.toList());

          newEmail.removeAll(this.emails);
          this.emails.addAll(0, newEmail);

          received.removeAll(this.emailReceived);
          this.emailReceived.addAll(0, received);
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  synchronized public void setEmailReadorNot(Email email, boolean read) {
    try {
      connectUser();
      boolean result = connection.setRead(email, read);
      if (result) {
        getEmailByUUID (email.getUuid()).setRead(read);
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  private Email getEmailByUUID (long id) {
    Email retrievedEmail = null;
    if (this.emails != null) {
      List<Email> selectedEmail = this.emails.stream().filter(e -> e.getUuid() == id).collect(Collectors.toList());
      if (selectedEmail != null && !selectedEmail.isEmpty()) {
        retrievedEmail = selectedEmail.get(0);
      }
    }
    return retrievedEmail;
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

  // TODO capire se serve invocare retrieveEmails o no
  public ObservableList<Email> getEmails() {
    retrieveEmails();
    return this.emails;
  }

  public ObservableList<Email> getEmailsSent() {
    return this.emailsSent;
  }

  public ObservableList<Email> getEmailReceived() {
    return this.emailReceived;
  }

  public void closeConnection() {
    if (this.connection != null) {
      connection.close();
      System.out.println("Connessione chiusa");
    }
  }

  // String... stringUtenti
  // in questo modo sarebbe possibile passare una o più stringhe, non so se possa servire
  public List<User> stringToUserList(String string) {
    List<User> userList = new ArrayList<>();
    String[] userNames = string.split("\\s+");
    for (String email: userNames) {
      userList.add(new User(email));
    }
    return userList;
  }

  public void setCurrentEmail(EmailProperty emailProperty) {
    this.currentEmailSelected.set(emailProperty);
  }

  public SimpleObjectProperty<EmailProperty> getCurrentEmailSelected() {
    return this.currentEmailSelected;
  }

  public boolean matchesEmailFormat(String email) {
    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    return email.matches(regex);
  }

}

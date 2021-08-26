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
    Thread emailRefreshThread = new Thread(new Runnable() {
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
    //emailRefreshThread.setDaemon(true); // TODO capire a cosa serve setDeamon
    emailRefreshThread.start();
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
      if (message.contains("successfully")) {
        email.setRead(true);
        this.emails.add(0, email);
        this.emailsSent.add(0, email);
      }
    }
    return message;
  }

  // TODO cambiare modo per verificare il successo di un operazione: usare true/false
  public String requestDeleteEmail (long uuid) throws IOException{
    String message = "";

    connectUser();
    Email email = getEmailByUUID(uuid);
    if (email != null) {
      message = this.connection.deleteEmail(email);
      if (message.contains("successfully")) {
        List<Email> emailToRemove = new ArrayList<>();
        emailToRemove.add(email);
        removeClientEmail (emailToRemove, this.emails);

        if (email.getSender().getUserName().equals(this.user.getUserName())) {
          removeClientEmail(emailToRemove, this.emailsSent);
        }
        if (email.recipientsAsString().contains(this.user.getUserName())) {
          removeClientEmail(emailToRemove, this.emailReceived);
        }
      }
    }
    return message;
  }

  // TODO dovrei prevenire errori
  private void removeClientEmail (List<Email> email, ObservableList<Email> emailList) {
    emailList.removeAll(email);
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
          List<Email> received = newEmail.stream().filter (e -> e.recipientsAsString().contains(this.user.getUserName())).distinct().collect(Collectors.toList());

          updateLists(received, this.emailReceived);
          updateLists(newEmail, this.emails);
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  private void updateLists (List<Email> newEmails, ObservableList<Email> emailList) {
    newEmails.removeAll(emailList);
    emailList.addAll(0, newEmails);
  }

  synchronized public boolean setEmailReadorNot(long uuid, boolean read) {
    Email email = getEmailByUUID(uuid);
    System.out.println(email);
    return setEmailReadorNot(email, read);
  }

  synchronized public boolean setEmailReadorNot(Email email, boolean read) {
    boolean result = false;
    try {
      connectUser();
      result = connection.setRead(email, read);
      if (result) {
        getEmailByUuid(this.emails, email.getUuid()).setRead(read);

        if (email.getSender().getUserName().equals(this.user.getUserName())) {
          getEmailByUuid(this.emailsSent, email.getUuid()).setRead(read);
        }
        if (email.recipientsAsString().contains(this.user.getUserName())) {
          getEmailByUuid(this.emailReceived, email.getUuid()).setRead(read);
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return result;
  }

  private Email getEmailByUUID (long id) {
    Email retrievedEmail = null;
    if (this.emails != null) {
      List<Email> selectedEmail = this.emails.stream().filter(e -> e.getUuid() == id).collect(Collectors.toList());
      if (!selectedEmail.isEmpty()) {
        // TODO potrebbero esserci più mail con lo stesso id
        retrievedEmail = selectedEmail.get(0);
      }
    }
    return retrievedEmail;
  }

  private Email getEmailByUuid (ObservableList<Email> emailList, long id) {
    Email retrievedEmail = null;
    if (emailList != null) {
      List<Email> selectedEmail = emailList.stream().filter(e -> e.getUuid() == id).collect(Collectors.toList());
      if (!selectedEmail.isEmpty()) {
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

  public ObservableList<Email> getEmails() {
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

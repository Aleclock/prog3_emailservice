package client;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lib.*;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static java.lang.Thread.sleep;

public class Model {
  private final Connection connection;
  private User user;
  private final ObservableList<Email> emails;
  private final ObservableList<Email> emailsSent;
  private final ObservableList<Email> emailReceived;
  private final SimpleObjectProperty<EmailProperty> currentEmailSelected = new SimpleObjectProperty<>(null);

  public Model() {
    this.connection = new Connection();
    this.emails = FXCollections.observableList(new ArrayList<>());
    this.emailsSent = FXCollections.observableList(new ArrayList<>());
    this.emailReceived = FXCollections.observableList(new ArrayList<>());
  }

  public void addListenerToConnectionStatus(ChangeListener<Boolean> cl) {
    this.connection.getConnectionStatus().addListener(cl);
  }

  public void removeListenerToConnectionStatus(ChangeListener<Boolean> cl) {
    this.connection.getConnectionStatus().removeListener(cl);
  }

  synchronized public boolean connectUser() {
    this.connection.connect();
    return this.connection.getConnectionStatus().get();
  }

  synchronized public void closeConnection() throws SocketException {
    this.connection.closeConnection();
  }


  /**
   * Create a thread that call retrieveEmails method every 5000 seconds, in order to update client emails list
   */
  public void refreshEmailList() {
    Thread emailRefreshThread = new Thread(new Runnable() {
      @Override
      public void run() {
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
    emailRefreshThread.setDaemon(true); // In questo modo il thread viene chiuso quando viene chiusa la finestra
    emailRefreshThread.start();
  }

  public OperationResponse requestSendMail(List<User> recipientsList, String subject, String body) throws IOException {
    OperationResponse result = new OperationResponse(false, "");
    if (recipientsList.isEmpty()) {
      result.setMessage(LabelMessage.client_sendEmail_noRecipient_error);
    } else {
      boolean connectionResult = connectUser();
      if (!connectionResult)
          result.setMessage(LabelMessage.serverDown);
      else {
        Email email = new Email(this.user, recipientsList, subject, body);
        result = this.connection.sendEmail(email);
        if (result.getResult()) {
          email = result.getEmail();
          email.setRead(true);
          result.setMessage(LabelMessage.client_sendEmail_success);
          this.emails.add(0, email);
          this.emailsSent.add(0, email);
        } else
          result.setMessage(LabelMessage.client_sendEmail_error);
      }

      closeConnection();
    }
    return result;
  }

  public OperationResponse requestDeleteEmail (long uuid) throws IOException {
    OperationResponse result = new OperationResponse(false, "");

    connectUser();
    Email email = getEmailByUUID(uuid);
    if (email != null) {
      result = this.connection.deleteEmail(email);
      if (result.getResult()) {
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

    closeConnection();
    return result;
  }

  private void removeClientEmail (List<Email> email, ObservableList<Email> emailList) {
    if (emailList != null && email != null) {
      emailList.removeAll(email);
    }
  }

  /*
  Si utilizza synchronized in modo tale che se più thread tentano di eseguire questo metodo, non lo fanno in contemporanea. Se
  il metodo è disponibile, il primo thread può accedere, mentre se è occupato il thread viene messo in coda fino a quando
  non è disponibile
   */
  synchronized public OperationResponse retrieveEmails() {
    OperationResponse result = new OperationResponse(false, "");
    try {
      if (connectUser()) {
        result = this.connection.getEmails();
        List<Email> newEmail = result.getEmailBox().getEmailList();

        if (newEmail != null) {
          if (this.emailsSent.isEmpty()) {
            List<Email> send = newEmail.stream().filter(e -> e.getSender().equals(this.user)).collect(Collectors.toList());
            this.emailsSent.addAll(send);
          }

          if (!newEmail.isEmpty()) {
            List<Email> received = newEmail.stream().filter(e -> e.recipientsAsString().contains(this.user.getUserName())).distinct().collect(Collectors.toList());

            updateLists(received, this.emailReceived);
            updateLists(newEmail, this.emails);
          }
        }

        closeConnection();
      } else {
        result.setMessage(LabelMessage.serverDown);
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }

    return result;
  }

  public void logout() {
    if (this.user != null) {
      connectUser();
      this.connection.logout();
    }
  }

  private void updateLists (List<Email> newEmails, ObservableList<Email> emailList) {
    newEmails.removeAll(emailList);
    emailList.addAll(0, newEmails);
  }

  synchronized public boolean setEmailReadorNot(long uuid, boolean read) {
    Email email = getEmailByUUID(uuid);
    return setEmailReadorNot(email, read);
  }

  synchronized public boolean setEmailReadorNot(Email email, boolean read) {
    boolean result = false;
    try {
      connectUser();
      result = connection.setRead(email, read).getResult();
      if (result) {
        getEmailByUuid(this.emails, email.getUuid()).setRead(read);

        if (email.getSender().getUserName().equals(this.user.getUserName())) {
          getEmailByUuid(this.emailsSent, email.getUuid()).setRead(read);
        }
        if (email.recipientsAsString().contains(this.user.getUserName())) {
          getEmailByUuid(this.emailReceived, email.getUuid()).setRead(read);
        }
      }
      closeConnection();
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

  // String... stringUtenti -- in questo modo sarebbe possibile passare una o più stringhe, non so se possa servire
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

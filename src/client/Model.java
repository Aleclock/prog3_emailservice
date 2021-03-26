package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lib.Email;
import lib.User;

import java.net.SocketException;
import java.util.ArrayList;

public class Model {
  private Connection connection;
  private User user;
  private final ObservableList<Email> mails;


  public Model() {
    this.connection = new Connection();
    this.mails = FXCollections.observableList(new ArrayList<Email>());
  }

  public void connectUser() throws SocketException {
    connection.connect();
    if (!connection.isConnected()) {
      throw new SocketException("Impossibile raggiungere il server");
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

}

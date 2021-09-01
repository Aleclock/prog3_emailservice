package server;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lib.*;

import java.io.*;
import java.net.Socket;

public class Connection implements Runnable{
  private PrintStream ps;
  private Model model;
  private Socket socket;
  private User user; // Questo varaibile è importante per la chiusura della connessione nel metodo closeConnection()
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private boolean closed = false;

  public Connection(Model model, Socket socket, PrintStream ps) {
    this.ps = ps;
    this.model = model;
    this.socket = socket;
    try {
      this.inputStream = new ObjectInputStream(socket.getInputStream());
      this.outputStream = new ObjectOutputStream(socket.getOutputStream());
      this.closed = false;
    } catch (IOException e){
      ps.println(LabelMessage.error_invalidSocket);
    }
  }

  @Override
  public void run() {
    try {
      Object o;
      if (!closed) {
        try {
          if ((o = inputStream.readObject()) != null) {
            if ( o instanceof Command) {
              Command command = (Command) o;
              setUser(command.getUser());
              handleCall(command);
            }
          }
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null && outputStream != null) {
        if (!closed)
          closeConnection();
      }
    }
  }

  private void setUser(User user) {
    this.user = user;
  }

  private void handleCall(Command command) throws IOException, ClassNotFoundException {
    if (!closed) {
      try {
        switch (command.getCommandKey()) {
          case "login":
            loginUser(command.getUser());
            break;
          case "logout":
            freeUser();
            break;
          case "read_emails":
            readEmails();
            break;
          case "send_email":
            sendEmail(command.getEmails());
            break;
          case "set_email_read":
            setEmailRead(command.getEmails(), command.getUser(), true);
            break;
          case "set_email_unread":
            setEmailRead(command.getEmails(), command.getUser(), false);
            break;
          case "delete_email":
            deleteEmail(command.getEmails(), command.getUser());
            break;
          default:
            break;
        }
      } catch (EOFException e) {
        //do nothing
      }
      closeConnection();
    }
  }

  public boolean isClosed() {
    return this.closed;
  }

  public void closeConnection() {
    if (!closed) {
      try {
        if (this.user != null) {
          ps.println(this.user.getUserName() + " " + LabelMessage.connectionClosed);
        }
        inputStream.close();
        outputStream.close();
        socket.close();
        this.user = null;
        this.model = null;
        this.closed = true;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void loginUser(User user) throws IOException {
    if (!closed) {
      OperationResponse<Boolean, String>  result = this.model.loginUser(user);
      outputStream.writeObject(result.getFirst());
      ps.println(user.getUserName() + " " + result.getSecond());
      if (result.getFirst()) {
        this.model.addUser(user);
        this.user = user;
        this.model.getOrCreateEmailBox(user);
      } else {
        closeConnection();
      }
    }
  }

  private void freeUser() {
    if (!closed) {
      this.model.freeUser(this.user);
      this.ps.println(user.getUserName() + " " + LabelMessage.server_userLogout);
    }
  }

  private void readEmails() throws IOException{
    if (!closed) {
      EmailBox emailBox = model.getEmailBox(this.user);
      if (emailBox != null) {
        this.ps.println(user.getUserName() + ": " + LabelMessage.server_readEmails_success);
      } else {
        this.ps.println(user.getUserName() + ": " + LabelMessage.server_readEmails_error);
      }
      this.outputStream.writeObject(emailBox);
    }
  }

  private void sendEmail(Email email) throws IOException{
    if (!closed) {
      OperationResponse<Boolean, String> result = this.model.sendEmail(email);
      this.outputStream.writeObject(result.getFirst());
      this.ps.println(this.user.getUserName() + " : " + result.getSecond());
    }
  }

  private void setEmailRead (Email email, User user, boolean read) throws IOException{
    if (!closed) {
      OperationResponse<Boolean, String> result = this.model.setEmailRead(user, email, read);
      this.outputStream.writeObject(result.getFirst());
      this.ps.println(result.getSecond());
    }
  }

  // TODO valutare di inviare al client non solo il valore booleano ma OperationResponse
  private void deleteEmail (Email email, User user) throws IOException{
    if (!closed) {
      OperationResponse<Boolean, String> result = this.model.deleteEmail(user, email);
      this.outputStream.writeObject(result.getFirst());
      this.ps.println(result.getSecond());
    }
  }
}

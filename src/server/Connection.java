package server;

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
            if (o.getClass() == Command.class) {
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
      OperationResponse result = this.model.loginUser(user);
      outputStream.writeObject(result);
      ps.println(user.getUserName() + " " + result.getMessage());
      if (result.getResult()) {
        this.user = user;
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
      OperationResponse result = model.getEmailBox(this.user);
      if (result.getResult()) {
        this.ps.println(user.getUserName() + ": " + LabelMessage.server_readEmails_success);
        result.setMessage(LabelMessage.server_readEmails_success);
      } else {
        this.ps.println(user.getUserName() + ": " + LabelMessage.server_readEmails_error);
        result.setMessage(LabelMessage.server_readEmails_error);
      }
      this.outputStream.writeObject(result);
    }
  }

  private void sendEmail(Email email) throws IOException{
    if (!closed) {
      OperationResponse result = this.model.sendEmail(email);
      this.outputStream.writeObject(result);
      this.ps.println(this.user.getUserName() + " : " + result.getMessage());
    }
  }

  private void setEmailRead (Email email, User user, boolean read) throws IOException{
    if (!closed) {
      OperationResponse result = this.model.setEmailRead(user, email, read);
      this.outputStream.writeObject(result);
      this.ps.println(result.getMessage());
    }
  }

  private void deleteEmail (Email email, User user) throws IOException{
    if (!closed) {
      OperationResponse result = this.model.deleteEmail(user, email);
      this.outputStream.writeObject(result);
      this.ps.println(result.getMessage());
    }
  }

  public boolean isClosed() {
    return this.closed;
  }
}

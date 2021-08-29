package server;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lib.Command;
import lib.Email;
import lib.EmailBox;
import lib.User;
import java.io.*;
import java.net.Socket;

public class Connection implements Runnable{
  private PrintStream ps;
  private Model model;
  private Socket socket;
  private User user;
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
      e.printStackTrace();
      // TODO invalid socket
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
              // TODO capire se ha senso mantenere this.user
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
      Object o;
      try {
        switch (command.getCommandKey()) {
          case "login":
            loginUser(command.getUser());
            break;
          case "close_connection":
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
          ps.println(this.user.getUserName() + " closed connection");
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

  // TODO se l'utente è già loggato dare errore
  private void loginUser(User user) throws IOException {
    if (!closed) {
      if (verifyUser(user)) {
        // TODO capire perchè viene fatta sta cosa
        this.model.addUser(user);
        this.user = user;
        outputStream.writeObject(true);
        this.model.getOrCreateEmailBox(this.user);
        ps.println(this.user.getUserName() + " logged in");
      } else {
        outputStream.writeObject(false);
        closeConnection();
      }
    }
  }

  private boolean verifyUser(User user) {
    return this.model.existUser(user);
  }

  private void freeUser() {
    if (!closed) {
      this.model.freeUser(this.user);
    }
  }

  private void readEmails() throws IOException{
    if (!closed) {
      EmailBox emailBox = model.getEmailBox(this.user);
      if (emailBox != null) {
        this.ps.println(user.getUserName() + ": lettura della casella email andata a buon fine");
      } else {
        this.ps.println(user.getUserName() + ": la lettura della casella email non è andata a buon fine");
      }
      this.outputStream.writeObject(emailBox);
    }
  }

  private void sendEmail(Email email) throws IOException{
    if (!closed) {
      boolean operation_result = this.model.sendEmail(email);
      this.outputStream.writeObject(operation_result);
      if (operation_result) {
        this.ps.println(this.user.getUserName() + " : sent successfully.");
      } else {
        this.ps.println(this.user.getUserName() + " : sending mail failed");
      }
    }
  }

  private void setEmailRead (Email email, User user, boolean read) throws IOException{
    if (!closed) {
      Boolean operation_result = this.model.setEmailRead(user, email, read);
      this.outputStream.writeObject(operation_result);

      String message;
      if (read) {
        message = email.getUuid() + " as read";
      } else {
        message = email.getUuid() + " as unread";
      }

      if (operation_result) {
        this.ps.println(this.user.getUserName() + " : set " + message);
      } else {
        this.ps.println(this.user.getUserName() + " : failure setting " + message);
      }
    }
  }

  private void deleteEmail (Email email, User user) throws IOException{
    if (!closed) {
      Boolean operation_result = this.model.deleteEmail(user, email);
      this.outputStream.writeObject(operation_result);
      if (operation_result) {
        this.ps.println(this.user.getUserName() + " : email " + email.getUuid() + " successfully deleted");
      } else {
        this.ps.println(this.user.getUserName() + " : ERROR while deleting email " + email.getUuid());
      }
    }
  }
}

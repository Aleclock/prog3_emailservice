package lib;

import java.io.Serializable;

public class Command implements Serializable {
  private final User user;
  private final String commandKey;
  private final Email email;

  public Command (User user, String command, Email email) {
    this.user = user;
    this.commandKey = command;
    this.email = email;
  }

  public String getCommandKey() {
    return this.commandKey;
  }

  public User getUser() {
    return this.user;
  }

  public Email getEmail() {
    return this.email;
  }
}

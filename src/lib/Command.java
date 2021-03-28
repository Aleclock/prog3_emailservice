package lib;

import java.io.Serializable;
import java.util.List;

public class Command implements Serializable {
  private User user;
  private String commandKey;
  private Email emails;

  public Command (User user, String command, Email emails) {
    this.user = user;
    this.commandKey = command;
    this.emails = emails;
  }

  public String getCommandKey() {
    return this.commandKey;
  }

  public User getUser() {
    return this.user;
  }

  public Email getEmails() {
    return this.emails;
  }
}

package lib;

import java.io.Serializable;

public class User implements Serializable {
  private String userName;

  public User(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return this.userName;
  }
}

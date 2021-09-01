package lib;

import java.io.Serializable;

public class User implements Serializable {
  private final String userName;

  public User(String userName) {
    this.userName = userName;
  }

  public String getUserName() {
    return this.userName;
  }

  @Override
  public boolean equals(Object obj) {
    boolean areEqual = false;
    if (obj.getClass() == User.class) {
      User user = (User) obj;
      areEqual = this.userName.equals(user.getUserName());
    }
    return areEqual;
  }
}

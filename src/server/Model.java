package server;

import lib.User;

import java.util.ArrayList;
import java.util.List;

public class Model {
  private List<User> userList = new ArrayList<>();

  public void addUser(User user) {
    this.userList.add(user);
  }

  public void freeUser(User user) {
    this.userList.remove(user);
    // TODO stampare in console che è stato effettuato il logout
  }
}

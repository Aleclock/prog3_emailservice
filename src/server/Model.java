package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lib.Email;
import lib.EmailBox;
import lib.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Model {
  private PrintStream ps;
  private List<User> userList = new ArrayList<>();
  private EmailBox emailBox;
  private String dataPath = "src/server/data/";

  Model(PrintStream ps){
    this.ps = ps;
  }

  public void getOrCreateEmailBox(User user) {
    this.emailBox = getEmailBox(user);
    if (this.emailBox == null) {
      createEmptyEmailBox(user);
      this.emailBox = getEmailBox(user);
    }
  }

  public EmailBox getEmailBox(User user) {
    EmailBox emailBox = null;
    String filePath = this.dataPath + user.getUserName() + ".json";
    File file = new File(filePath);
    if (file.exists() && file.isFile()) {
      BufferedReader jsonFile = null;
      try {
        jsonFile = new BufferedReader(new FileReader(filePath));
        Gson gson = new Gson();
        emailBox = gson.fromJson(jsonFile, EmailBox.class);
      } catch (FileNotFoundException e) {
        this.ps.println(user.getUserName() + ": la lettura della casella email non è andata a buon fine");
        e.printStackTrace();
      }
    }
    return emailBox;
  }

  private void createEmptyEmailBox(User user) {
    if (user != null) {
      String filePath = this.dataPath + user.getUserName() + ".json";
      List<Email> emailList = new ArrayList<>();
      EmailBox emailBox = new EmailBox(user, emailList);
      try {
        Writer writer = new FileWriter(filePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJsonTree(emailBox));
        gson.toJson(emailBox, writer);
        writer.close();
      } catch (IOException e) {
        this.ps.println(user.getUserName() + ": la creazione della casella mail non è andata a buon fine");
        e.printStackTrace();
      }
    }
  }

  public void addUser(User user) {
    this.userList.add(user);
  }

  public void freeUser(User user) {
    this.userList.remove(user);
    // TODO stampare in console che è stato effettuato il logout
  }
}

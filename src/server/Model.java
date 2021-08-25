package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lib.Email;
import lib.EmailBox;
import lib.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Model {
  final private String dataPath = "src/server/data/";
  private PrintStream ps; // TODO forse non è il model che si deve occupare della scrittura sull'interfaccia
  final private List<String> userList = new ArrayList<>();
  private List<User> connectedUser = new ArrayList<>();

  Model(PrintStream ps){
    this.ps = ps;
    initUserList();
  }

  public EmailBox getOrCreateEmailBox(User user) {
    EmailBox emailBox = getEmailBox(user);
    if (emailBox == null) {
      createEmptyEmailBox(user);
      emailBox = getEmailBox(user);
    }
    return emailBox;
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

  public boolean sendEmail(Email email) {
    boolean done = false;
    List<Email> emails = new ArrayList<>();
    emails.add(email);

    for (User recipient : email.getRecipients()) {
      if (existUser(recipient)) {
        EmailBox emailboxRecipient = getOrCreateEmailBox(recipient);
        done = addEmailToEmailBox(recipient, emailboxRecipient, emails);
      } else {
        this.ps.println(recipient.getUserName() + " not exist: Sending mail failed");
        List<Email> errorEmail = new ArrayList<>();
        errorEmail.add(createErrorEmail(email, recipient));
        addEmailToEmailBox(email.getSender(), getEmailBox(email.getSender()), errorEmail);
      }
    }
    if (done && !email.recipientsAsString().contains(email.getSender().getUserName())) {
      int emailIndex = emails.indexOf(email);
      emails.get(emailIndex).setRead(true);
      done = addEmailToEmailBox(email.getSender(), getEmailBox(email.getSender()), emails);
    }
    return done;
  }

  public boolean deleteEmail (User user, Email email) {
    boolean done = false;
    EmailBox emailBox = getEmailBox(user);
    if (emailBox != null) {
      List<Email> emails = emailBox.getEmailList();
      emails.remove(email);
      done = writeEmailBoxAsJSON(emailBox, user);
    }
    return done;
  }

  private Email createErrorEmail(Email email, User userNonExistent) {
    User systemUser = new User ("Mail Delivery System");
    String bodyMessage = "This is a system-generated message to inform you that your email could not be delivered to following" +
            "recipients. Details of the email and the error are as follows:\n" +
            userNonExistent.getUserName() + " not exist.\n\n" +
            "EMAIL INFO:\n" +
            "Subject: " + email.getSubject() + "\n" +
            "Date sent: " + email.getDateSent() + "\n" +
            "Body: " + email.getBody();
    return new Email(systemUser, email.getSender(), "Undelivered Mail Returned to Sender", bodyMessage);
  }

  public boolean setEmailRead (User user, Email email, boolean read) {
    boolean done = false ;
    EmailBox emailBox = getEmailBox(user);
    if (emailBox != null) {
      List<Email> emails = emailBox.getEmailList();
      int emailIndex = emails.indexOf(email);
      emails.get(emailIndex).setRead(read);
      done = writeEmailBoxAsJSON(emailBox, user);
    }
    return done;
  }

  // TODO c'è la lista di Email perchè forse potrebbero esserci più email da inviare nello stesso momento, non so da vedere
  private boolean addEmailToEmailBox(User user, EmailBox emailBox, List<Email> emails) {
    boolean done = false;
    emailBox.addEmails(emails);
    // TODO usare writeEmailBoxAsJSON
    String filePath = this.dataPath + user.getUserName() + ".json";
    try {
      Writer writer = new FileWriter(filePath);
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      gson.toJson(emailBox, writer);
      writer.close();
      done = true;
    }  catch (IOException e) {
      this.ps.println(user.getUserName() + ": scrittura della nuova mail fallita");
      e.printStackTrace();
    }
    return done;
  }

  private void createEmptyEmailBox(User user) {
    if (user != null) {
      List<Email> emailList = new ArrayList<>();
      EmailBox emailBox = new EmailBox(user, emailList);
      boolean success = writeEmailBoxAsJSON(emailBox, user);
      if (!success) {
        this.ps.println(user.getUserName() + ": la creazione della casella mail non è andata a buon fine");
      }
    }
  }

  private boolean writeEmailBoxAsJSON (EmailBox emailBox, User user) {
    boolean success = false;
    if (user != null) {
      String filePath = this.dataPath + user.getUserName() + ".json";
      try {
        Writer writer = new FileWriter(filePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(emailBox, writer);
        success = true;
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return success;
  }

  public boolean existUser(User user) {
    return this.userList.contains(user.getUserName());
  }

  public void addUser(User user) {
    this.connectedUser.add(user);
  }

  // TODO controllare che venga scritto "logout" in console server
  public void freeUser(User user) {
    this.connectedUser.remove(user);
    this.ps.println(user.getUserName() + " logout");
  }

  private void initUserList(){
    File file = new File("src/server/data/users.txt");
    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String email = scanner.nextLine();
        this.userList.add(email);
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}

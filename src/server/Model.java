package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lib.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Model {
  final private String dataPath = "src/server/data/";
  final private List<String> userList = new ArrayList<>();
  private List<User> connectedUser = new ArrayList<>();

  Model(){
    initUserList();
  }

  public OperationResponse<Boolean, String> loginUser(User user) {
    OperationResponse<Boolean, String> result = new OperationResponse<>(false, "");

    if (existUser(user)) {
      result.set(true, LabelMessage.server_userLogin);
      if (isAlreadyLogged(user)) {
        result.set(false, LabelMessage.server_userLogin_alreadyLogged_error);
      }
    } else {
      result.set(false, LabelMessage.server_userLogin_noExist_error);
    }
    return result;
  }

  // TODO questo potrebbe dare problemi, non ci sono controlli
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
      BufferedReader jsonFile;
      try {
        jsonFile = new BufferedReader(new FileReader(filePath));
        Gson gson = new Gson();
        emailBox = gson.fromJson(jsonFile, EmailBox.class);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return emailBox;
  }

  public OperationResponse<Boolean, String> sendEmail(Email email) {
    OperationResponse<Boolean, String> result = new OperationResponse<>(false, "");
    String message = "";
    List<Email> emails = new ArrayList<>();
    emails.add(email);

    for (User recipient : email.getRecipients()) {
      if (existUser(recipient)) {
        EmailBox emailboxRecipient = getOrCreateEmailBox(recipient);
        result.setFirst(addEmailToEmailBox(recipient, emailboxRecipient, emails));
      } else {
        message = recipient.getUserName() + " not exist: Sending mail failed\n";
        List<Email> errorEmail = new ArrayList<>();
        errorEmail.add(createErrorEmail(email, recipient));
        addEmailToEmailBox(email.getSender(), getEmailBox(email.getSender()), errorEmail);
      }
    }
    if (result.getFirst() && !email.recipientsAsString().contains(email.getSender().getUserName())) {
      int emailIndex = emails.indexOf(email);
      emails.get(emailIndex).setRead(true);
      result.setFirst(addEmailToEmailBox(email.getSender(), getEmailBox(email.getSender()), emails));
    }

    if (result.getFirst()){
      message += "\t" + LabelMessage.server_sendEmail_success;
    } else {
      message += "\t" + LabelMessage.server_sendEmail_error;
    }
    result.setSecond(message);
    return result;
  }

  public OperationResponse<Boolean, String> deleteEmail (User user, Email email) {
    OperationResponse<Boolean, String> result = new OperationResponse<>(false, "");
    EmailBox emailBox = getEmailBox(user);
    if (emailBox != null) {
      List<Email> emails = emailBox.getEmailList();
      emails.remove(email);
      result.setFirst(writeEmailBoxAsJSON(emailBox, user));
    }

    if (result.getFirst()) {
      result.setSecond(user.getUserName() + " : email " + email.getUuid() + " " + LabelMessage.server_deleteEmail_success);
    } else {
      result.setSecond(user.getUserName() + " : " + LabelMessage.server_deleteEmail_error + " " + email.getUuid());
    }
    return result;
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

  public OperationResponse<Boolean, String> setEmailRead (User user, Email email, boolean read) {
    OperationResponse<Boolean, String> result = new OperationResponse<>(false, "");
    EmailBox emailBox = getEmailBox(user);
    if (emailBox != null) {
      List<Email> emails = emailBox.getEmailList();
      int emailIndex = emails.indexOf(email);
      emails.get(emailIndex).setRead(read);
      result.setFirst(writeEmailBoxAsJSON(emailBox, user));

      String message = user.getUserName();
      if (result.getFirst()) {
        message += " : set ";
      } else {
        message += " : failure setting ";
      }

      if (read) {
        message += " as read";
      } else {
        message += " as unread";
      }

      result.setSecond(message);
    }
    return result;
  }

  // TODO c'è la lista di Email perchè forse potrebbero esserci più email da inviare nello stesso momento, non so da vedere
  private boolean addEmailToEmailBox(User user, EmailBox emailBox, List<Email> emails) {
    emailBox.addEmails(emails);
    return writeEmailBoxAsJSON(emailBox, user);
  }

  private void createEmptyEmailBox(User user) {
    if (user != null) {
      List<Email> emailList = new ArrayList<>();
      EmailBox emailBox = new EmailBox(user, emailList);
      writeEmailBoxAsJSON(emailBox, user);
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

  public boolean isAlreadyLogged(User user) {
    return this.connectedUser.contains(user);
  }

  public void addUser(User user) {
    this.connectedUser.add(user);
  }

  public void freeUser(User user) {
    this.connectedUser.remove(user);
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

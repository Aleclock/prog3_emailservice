package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lib.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Model {
  final private String dataPath = "src/server/data/";
  final private List<String> userList = new ArrayList<>();
  private final List<User> connectedUser = new ArrayList<>();
  private final UUIDGenerator uuidGenerator;

  private static final StringProperty stringOpenConnectionProperty = new SimpleStringProperty(Integer.toString(0));

  Model(){
    initUserList();
    this.uuidGenerator = new UUIDGenerator(dataPath);
  }

  public static StringProperty getOpenConnectionProperty() {
    return stringOpenConnectionProperty;
  }

  public OperationResponse loginUser(User user) {
    OperationResponse result = new OperationResponse(false, "");

    if (existUser(user)) {
      result.set(true, LabelMessage.server_userLogin);
      if (isAlreadyLogged(user)) {
        result.set(false, LabelMessage.server_userLogin_alreadyLogged_error);
      }
    } else {
      result.set(false, LabelMessage.server_userLogin_noExist_error);
    }

    if (result.getResult()) {
      addUser(user);
      result.setEmailBox(getOrCreateEmailBox(user).getEmailBox());
    }

    return result;
  }

  public OperationResponse getOrCreateEmailBox(User user) {
    OperationResponse result = getEmailBox(user);
    if (!result.getResult()) {
      createEmptyEmailBox(user);
      result = getEmailBox(user);
    }
    return result;
  }

  public OperationResponse getEmailBox(User user) {
    OperationResponse result = new OperationResponse(false, "");
    String filePath = this.dataPath + user.getUserName() + ".json";
    File file = new File(filePath);
    if (file.exists() && file.isFile()) {
      BufferedReader jsonFile;
      try {
        jsonFile = new BufferedReader(new FileReader(filePath));
        Gson gson = new Gson();
        result.set(true, LabelMessage.server_getOrCreateEmailBox_success, gson.fromJson(jsonFile, EmailBox.class));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        result.set(false, LabelMessage.server_getOrCreateEmailBox_error);
      }
    }
    return result;
  }

  synchronized public OperationResponse sendEmail(Email email) {
    OperationResponse result = new OperationResponse(false, "");
    email.setUUID(this.uuidGenerator.generateUUID());
    String message = "";

    for (User recipient : email.getRecipients()) {
      if (existUser(recipient)) {
        OperationResponse emailboxRecipient_response = getOrCreateEmailBox(recipient);
        if (emailboxRecipient_response.getEmailBox() != null) {
          result.setResult(addEmailToEmailBox(recipient, emailboxRecipient_response.getEmailBox(), email));
        }
      } else {
        message = recipient.getUserName() + " " + LabelMessage.server_sendEmail_userNotExist;
        Email errorEmail = createErrorEmail(email, recipient);
        addEmailToEmailBox(email.getSender(), getEmailBox(email.getSender()).getEmailBox(), errorEmail);
      }
    }
    if (result.getResult() && !email.recipientsAsString().contains(email.getSender().getUserName())) {
      email.setRead(true);
      result.setResult(addEmailToEmailBox(email.getSender(), getEmailBox(email.getSender()).getEmailBox(), email));
    }

    if (result.getResult()){
      message += "\t" + LabelMessage.server_sendEmail_success;
    } else {
      message += "\t" + LabelMessage.server_sendEmail_error;
    }

    result.setEmail(email);
    result.setMessage(message);
    return result;
  }

  public OperationResponse deleteEmail (User user, Email email) {
    OperationResponse result = new OperationResponse(false, "");
    OperationResponse emailBox_response = getEmailBox(user);
    //EmailBox emailBox = getEmailBox(user);

    if (emailBox_response.getResult()) {
      List<Email> emails = emailBox_response.getEmailBox().getEmailList();
      emails.remove(email);
      result.setResult(writeEmailBoxAsJSON(emailBox_response.getEmailBox(), user));
    }

    if (result.getResult()) {
      result.setMessage(user.getUserName() + " : email " + email.getUuid() + " " + LabelMessage.server_deleteEmail_success);
    } else {
      result.setMessage(user.getUserName() + " : " + LabelMessage.server_deleteEmail_error + " " + email.getUuid());
    }
    return result;
  }

  public OperationResponse setEmailRead (User user, Email email, boolean read) {
    OperationResponse result = getEmailBox(user);
    if (result.getResult()) {
      List<Email> emails = result.getEmailBox().getEmailList();
      int emailIndex = emails.indexOf(email);
      emails.get(emailIndex).setRead(read);
      result.setResult(writeEmailBoxAsJSON(result.getEmailBox(), user));

      String message = user.getUserName();
      if (result.getResult()) {
        message += " : set " + email.getUuid();
      } else {
        message += " : failure setting " + email.getUuid();
      }

      if (read) {
        message += " as read";
      } else {
        message += " as unread";
      }

      result.setMessage(message);
    }
    return result;
  }

  // --------------------------------
  // -- Useful methods
  // --------------------------------

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

  private boolean addEmailToEmailBox(User user, EmailBox emailBox, Email email) {
    emailBox.addEmail(email);
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
    Platform.runLater(() -> stringOpenConnectionProperty.setValue(String.valueOf(this.connectedUser.size())));
  }

  public void freeUser(User user) {
    this.connectedUser.remove(user);
    Platform.runLater(() -> stringOpenConnectionProperty.setValue(String.valueOf(this.connectedUser.size())));
  }

  private void initUserList() {
    File file = new File( this.dataPath + "users.txt");
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

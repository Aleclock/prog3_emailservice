package lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Email implements Serializable {
  private int uuid;
  private User sender;
  private List<User> recipients;
  private Long dateSent;
  private String subject;
  private String body;
  private boolean read;

  public Email(User sender, List<User> recipients, Long dateSent, String subject, String body) {
    setValues(sender, recipients, dateSent, subject, body);
  }

  public Email (User sender, User recipients, Long dateSent, String subject, String body) {
    List<User> recipientsList = new ArrayList<>();
    recipientsList.add(recipients);
    setValues(sender, recipientsList, dateSent, subject, body);
  }

  private void setValues(User sender, List<User> recipients, Long dateSent, String subject, String body) {
    this.uuid = 0;
    this.sender = sender;
    this.recipients = recipients;
    this.dateSent = dateSent;
    this.subject = subject;
    this.body = body;
    this.read = false;
  }

  public int getUuid() {
    return this.uuid;
  }

  public User getSender() {
    return this.sender;
  }

  public List<User> getRecipients() {
    return this.recipients;
  }

  public Long getDateSent() {
    return this.dateSent;
  }

  public String getSubject() {
    return this.subject;
  }

  public String getBody() {
    return this.body;
  }

  public boolean hasBeenRead() {
    return this.read;
  }

  public void setRead(boolean r) {
    this.read = r;
  }
}

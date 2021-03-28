package lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Email implements Serializable {
  private long uuid;
  private User sender;
  private List<User> recipients;
  private Date dateSent;
  private String subject;
  private String body;
  private boolean read;

  // TODO capire se ha più senso impostare l'id a livello di server o quando viene creato l'oggetto Email

  public Email(User sender, List<User> recipients, String subject, String body) {
    setValues(sender, recipients, new Date(), subject, body);
  }

  public Email (User sender, User recipients, String subject, String body) {
    List<User> recipientsList = new ArrayList<>();
    recipientsList.add(recipients);
    setValues(sender, recipientsList, new Date(), subject, body);
  }

  private void setValues(User sender, List<User> recipients, Date dateSent, String subject, String body) {
    this.sender = sender;
    this.recipients = recipients;
    this.dateSent = dateSent;
    this.subject = subject;
    this.body = body;
    this.read = false;
    this.uuid = setUuid();
    System.out.println(setUuid());
  }

  // TODO trovare un modo per impostare l'id
  public long setUuid() {
    Long id = 0L;
    id += this.sender.hashCode();
    id += this.recipients.hashCode();
    id += this.dateSent.hashCode();
    id += this.subject.hashCode();
    id += this.body.hashCode();
    return id;
  }

  public long getUuid() {
    return this.uuid;
  }

  public User getSender() {
    return this.sender;
  }

  public List<User> getRecipients() {
    return this.recipients;
  }

  public Date getDateSent() {
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

  public String recipientsAsString() {
    return this.recipients.stream().map(User::getUserName).collect(Collectors.joining(" , "));
  }

  @Override
  public boolean equals(Object obj) {
    boolean areEqual = false;
    if (obj instanceof Email) {
      Email email = (Email) obj;
      if (email.uuid == this.uuid) {
        areEqual = true;
      }
    }
    return areEqual;
  }
}

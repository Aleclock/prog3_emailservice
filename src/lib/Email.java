package lib;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  https://stackoverflow.com/questions/18791566/notserializableexception-on-simplelistproperty
 */
public class Email implements Serializable {
  private long uuid;
  private User sender;
  private List<User> recipients;
  private Date dateSent;
  private String subject;
  private String body;
  private boolean read;

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
  }

  public void setUUID(long uuid) {
    this.uuid = uuid;
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

  public String getFormattedDate () {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    return formatter.format(this.dateSent);
  }

  public String getRecipientsAsList() {
    StringBuilder s = new StringBuilder();
    for (User r : this.recipients) {
      s.append(" ").append(r.getUserName());
    }
    return s.toString();
  }

  @Override
  public String toString() {
    return "Email{" +
            "ID : " + this.uuid +
            ", data : " + this.dateSent +
            ", mittente : " + this.sender +
            ", destinatario : " + this.recipients +
            ", Oggetto : '" + this.subject + '\'' +
            ", testo : '" + this.body + '\'' +
            ", letta : " + this.read +
            '}';
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

  @Override
  public int hashCode() {
    return (int) this.uuid;
  }
}

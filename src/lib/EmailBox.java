package lib;

import java.io.Serializable;
import java.util.List;

public class EmailBox implements Serializable {
  private User user;
  private List<Email> emailList;

  public EmailBox(User user, List<Email> emailList) {
    this.user = user;
    this.emailList = emailList;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void addEmail(Email email) {
    this.emailList.add(0, email);
  }

  public void addEmails(List<Email> emails) {
    this.emailList.addAll(0, emails);
  }

  public void setEmailList(List<Email> emails) {
    this.emailList = emails;
  }

  public User getUser() {
    return this.user;
  }

  public List<Email> getEmailList() {
    return this.emailList;
  }

  @Override
  public String toString() {
    StringBuilder emailsString = new StringBuilder();
    emailsString.append("User: ").append(this.user.getUserName()).append("\n");
    emailsString.append("Emails:\n");
    for (Email email: this.emailList) {
      emailsString.append(email.getDateSent() + " - " + email.getSubject() + "\n");
      emailsString.append("Sender:" + email.getSender() + "\n");
      emailsString.append("Recipients: " + email.getRecipients() + "\n");
      emailsString.append("Body: " + email.getBody() + "\n\n");
    }
    return emailsString.toString();
  }
}

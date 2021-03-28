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

  // TODO valutare se aggiungere il setter

  public void addEmails(List<Email> emails) {
    this.emailList.addAll(0, emails);
  }

  public User getUser() {
    return this.user;
  }

  public List<Email> getEmailList() {
    return this.emailList;
  }

  @Override
  public String toString() {
    StringBuilder emails = new StringBuilder();
    emails.append("User: ").append(this.user.getUserName()).append("\n");
    emails.append("Emails:\n");
    for (Email email: this.emailList) {
      emails.append(email.getDateSent() + " - " + email.getSubject());
      // TODO aggiungere tutto
    }
    return emails.toString();
  }
}

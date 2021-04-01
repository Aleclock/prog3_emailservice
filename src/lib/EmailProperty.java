package lib;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// TODO qui ho fatto un po' di casino per inserire l'uuid, vedere se funziona con i vari cast
public class EmailProperty {
  private final StringProperty recipients = new SimpleStringProperty();
  private final StringProperty uuid = new SimpleStringProperty();
  private final StringProperty sender = new SimpleStringProperty();
  private final StringProperty subject = new SimpleStringProperty();
  private final StringProperty body = new SimpleStringProperty();
  private final StringProperty date = new SimpleStringProperty();

  public EmailProperty(Email email) {
    setRecipients(email.recipientsAsString());
    setUuid(email.getUuid());
    setSubject(email.getSubject());
    setSender(email.getSender().getUserName());
    setDate(email.getDateSent().toString());
    setBody(email.getBody());
  }

  public final StringProperty getRecipientsProperty() {
    return this.recipients;
  }

  public final StringProperty getSubjectProperty() {
    return this.subject;
  }

  public final StringProperty getUuidProperty() {
    return this.uuid;
  }

  public final StringProperty getDateProperty() {
    return this.date;
  }

  public final StringProperty getSenderProperty() {
    return this.sender;
  }

  public final StringProperty getBodyProperty() {
    return this.body;
  }

  public String getRecipients() {
    return getRecipientsProperty().get();
  }

  public String getSubject() {
    return getSubjectProperty().get();
  }

  public String getDate() {
    return getDateProperty().get();
  }

  public long getUuid() {
    return Long.parseLong(getUuidProperty().get());
  }

  public String getBody() {
    return getBodyProperty().get();
  }

  public String getSender() {
    return getSenderProperty().get();
  }

  public void setUuid (long uuid) {
    this.getUuidProperty().set(String.valueOf(uuid));
  }

  public void setRecipients (String recipients) {
    this.getRecipientsProperty().set(recipients);
  }

  public void setSubject (String subject) {
    this.getSubjectProperty().set(subject);
  }

  public void setSender (String username) {
    this.getSenderProperty().set(username);
  }

  public void setBody (String body) {
    this.getBodyProperty().set(body);
  }

  public void setDate (String date) {
    this.getDateProperty().set(date);
  }
}

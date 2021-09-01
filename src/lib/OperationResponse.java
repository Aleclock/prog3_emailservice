package lib;

import java.io.Serializable;

public class OperationResponse implements Serializable {
  private boolean result;
  private String message;
  private Email email;
  private EmailBox emailBox;

  public OperationResponse(boolean r, String m) {
    this.result = r;
    this.message = m;
  }

  public boolean getResult() {
    return this.result;
  }

  public String getMessage() {
    return this.message;
  }

  public Email getEmail() {
    return this.email;
  }

  public EmailBox getEmailBox() {
    return this.emailBox;
  }

  public void set(boolean result, String message) {
    this.result = result;
    this.message = message;
  }

  public void set(boolean result, String message, Email email) {
    set(result, message);
    this.email = email;
  }

  public void set(boolean result, String message, EmailBox emailBox) {
    set(result, message);
    this.emailBox = emailBox;
  }

  public void setResult(boolean r) {
    this.result = r;
  }

  public void setMessage(String m) {
    this.message = m;
  }

  public void setEmail(Email e) {
    this.email = e;
  }

  public void setEmailBox(EmailBox eb) {
    this.emailBox = eb;
  }
}

package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

// TODO impostare sender
public class NewEmailController {
  @FXML
  TextField tf_recipients, tf_subject;
  @FXML
  TextArea ta_body;
  @FXML
  Button send_email;
  @FXML
  Label label_email_status, label_sender;

  private Model model;

  public void initModel(Model model) {
    this.model = model;
  }

  public void setSender (String sender) {
    this.label_sender.setText(sender);
  }

  public void setRecipients(String recipients) {
    this.tf_recipients.setText(recipients);
  }

  public void setSubject(String subject) {
    this.tf_subject.setText(subject);
  }

  public void setBody(String body) {
    this.ta_body.setText(body);
  }

  // TODO controllare che le email dei destinatari siano formalmente corretti
  @FXML
  public void handleSendEmail(ActionEvent event) {
    String body = this.ta_body.getText();
    String recipients = this.tf_recipients.getText();
    String subject = this.tf_subject.getText();
    if (subject.equals("")) {
      subject = "(no subject)";
    }

    try {
      String message = this.model.requestSendMail(recipients, subject, body);
      this.label_email_status.setText(message);
      // TODO cambiare colore dello sfondo in base al messaggio
    } catch(IOException e) {
      // TODO gestire errori e messaggi
    }
  }

}

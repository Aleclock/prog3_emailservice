package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

  private Model model;

  public void initModel(Model model) {
    this.model = model;
  }

  public void setRecipients(String recipients) {
    tf_recipients.setText(recipients);
  }

  public void setSubject(String subject) {
    tf_subject.setText(subject);
  }

  public void setBody(String body) {
    ta_body.setText(body);
  }

  // TODO controllare che le email dei destinatari siano formalmente corretti
  @FXML
  public void handleSendEmail(ActionEvent event) {
    String body = ta_body.getText();
    String recipients = tf_recipients.getText();
    String subject = tf_subject.getText();
    if (subject.equals("")) {
      subject = "(no subject)";
    }

    try {
      String message = this.model.requestSendMail(recipients, subject, body);
      // TODO stampare messaggio da qualche parte
      // TODO valutare se aggiungere direttamente la mail alla lista se tutto è andato bene
    } catch(IOException e) {
      // TODO gestire errori e messaggi
    }
  }

}

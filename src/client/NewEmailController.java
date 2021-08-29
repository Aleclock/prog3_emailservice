package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lib.ColorManager;
import lib.LabelMessage;
import lib.User;

import java.io.IOException;
import java.util.List;

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

  @FXML
  public void handleSendEmail(ActionEvent event) {
    String body = this.ta_body.getText();
    String recipients = this.tf_recipients.getText();
    String subject = this.tf_subject.getText();
    if (subject.equals("")) {
      subject = "(no subject)";
    }

    try {
      List<User> recipientsUser = this.model.stringToUserList(recipients);
      boolean correct = true;
      for (User u : recipientsUser) {
        if (!this.model.matchesEmailFormat(u.getUserName())) {
          correct = false;
        }
      }

      if (correct) {
        String message = this.model.requestSendMail(recipientsUser, subject, body);
        String cssValue;
        if (message.contains("successfully")) {
          // TODO valutare se chiudere la finestra nel caso in cui l'email sia stata inviata correttamente
          cssValue = "-fx-background-color: " + ColorManager.successColor;
        } else {
          cssValue = "-fx-background-color: " + ColorManager.errorColor;
        }
        this.label_email_status.setStyle(cssValue);
        this.label_email_status.setText(message);
      } else {
        String cssValue = "-fx-background-color: " + ColorManager.errorColor;
        this.label_email_status.setStyle(cssValue);
        this.label_email_status.setText(LabelMessage.client_sendEmail_emailNotValid_error);

        // TODO andare in sleep per tot secondi e poi chiudere la finestra
      }
    } catch(IOException e) {
      // TODO gestire errori e messaggi
    }
  }

}

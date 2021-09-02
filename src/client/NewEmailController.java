package client;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
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
  private Stage stage;

  public void initModel(Model model) {
    this.model = model;
  }

  public void setStage(Stage s) {
    this.stage = s;
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
          cssValue = LabelMessage.css_backgroundColor + ColorManager.successColor;

          PauseTransition delay = new PauseTransition(Duration.seconds(2));
          delay.setOnFinished(e -> this.stage.close());
          delay.play();
        } else {
          cssValue = LabelMessage.css_backgroundColor + ColorManager.errorColor;
        }
        this.label_email_status.setStyle(cssValue);
        this.label_email_status.setText(message);
      } else {
        String cssValue = LabelMessage.css_backgroundColor + ColorManager.errorColor;
        this.label_email_status.setStyle(cssValue);
        this.label_email_status.setText(LabelMessage.client_sendEmail_emailNotValid_error);
      }

      removeLabelMessage();
    } catch(IOException e) {
      // TODO gestire errori e messaggi
    }
  }

  private void removeLabelMessage() {
    PauseTransition delayLog = new PauseTransition(Duration.seconds(4));
    delayLog.setOnFinished(e -> {
      this.label_email_status.setText("");
      this.label_email_status.setStyle(null);
    });
    delayLog.play();
  }

}

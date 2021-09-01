package client;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import jdk.dynalink.Operation;
import lib.ColorManager;
import lib.EmailProperty;
import lib.LabelMessage;
import lib.OperationResponse;

import java.io.IOException;

public class EmailDetailController {
  private Model model;
  @FXML
  private Label label_recipipients, label_subject, label_sender, label_date, label_body;
  @FXML
  private Label label_log;

  public void initModel(Model model) {
    this.model = model;
    this.model.getCurrentEmailSelected().addListener( (obs, oldEmail, newEmail) -> {
      if (newEmail != null) {
        this.label_recipipients.textProperty().bindBidirectional(newEmail.getRecipientsProperty());
        this.label_subject.textProperty().bindBidirectional(newEmail.getSubjectProperty());
        this.label_sender.textProperty().bindBidirectional(newEmail.getSenderProperty());
        this.label_date.textProperty().bindBidirectional(newEmail.getDateProperty());
        this.label_body.textProperty().bindBidirectional(newEmail.getBodyProperty());
      }
    });
  }

  @FXML
  public void handleReplyAll() {
    NewEmailController controller = loadNewMailScene();
    if (controller != null) {
      String recipients = this.label_recipipients.textProperty().get();
      recipients = recipients.replace(" , ", " ");
      recipients += " " + this.label_sender.textProperty().get();
      recipients = recipients.replace(this.model.getUser().getUserName() + " ", "");
      controller.setRecipients(recipients);
      controller.setSubject("RE: " + this.label_subject.textProperty().get());
    }
  }

  @FXML
  public void handleDeleteEmail () {
    try {
      EmailProperty emailProperty = this.model.getCurrentEmailSelected().get();
      OperationResponse result = this.model.requestDeleteEmail(emailProperty.getUuid());

      String cssValue;
      if (result.getResult()) {
        cssValue = LabelMessage.css_backgroundColor + ColorManager.successColor;
        clearAll();
      } else {
        cssValue = LabelMessage.css_backgroundColor + ColorManager.errorColor;
      }
      this.label_log.setStyle(cssValue);
      this.label_log.setText(result.getMessage());
      removeLabelMessage(this.label_log, Duration.seconds(2));
    } catch (IOException e) {
      this.label_log.setStyle(LabelMessage.css_backgroundColor + ColorManager.errorColor);
      this.label_log.setText(e.getMessage());
    }

  }

  @FXML
  public void handleReply () {
    NewEmailController controller = loadNewMailScene();
    if (controller != null) {
      controller.setRecipients(this.label_sender.textProperty().get());
      controller.setSubject("RE: " + this.label_subject.textProperty().get());
    }
  }

  @FXML
  public void handleForward () {
    NewEmailController controller = loadNewMailScene();
    if (controller != null) {
      controller.setSubject(this.label_subject.textProperty().get());
      controller.setBody(this.label_body.textProperty().get());
    }
  }

  @FXML
  public void handleReadUnread() {
    EmailProperty emailProperty = this.model.getCurrentEmailSelected().get();
    boolean result = this.model.setEmailReadorNot(emailProperty.getUuid(), false);
    String cssValue;
    if (result) {
      this.label_log.setText(LabelMessage.client_emailSetUnread_success);
      cssValue = LabelMessage.css_backgroundColor + ColorManager.successColor;
    } else {
      this.label_log.setText(LabelMessage.client_emailSetUnread_error);
      cssValue = LabelMessage.css_backgroundColor + ColorManager.errorColor;
    }
    this.label_log.setStyle(cssValue);
    removeLabelMessage(this.label_log, Duration.seconds(2));
  }

  private void clearAll() {
    this.label_recipipients.setText("");
    this.label_body.setText("");
    this.label_sender.setText("");
    this.label_subject.setText("");
    this.label_date.setText("");
  }

  private NewEmailController loadNewMailScene() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("res/new_email_scene.fxml"));
      Scene scene = new Scene(loader.load());
      Stage stage = new Stage();
      stage.setTitle(this.model.getUser().getUserName() + " - New email");
      stage.setScene(scene);
      stage.show();

      NewEmailController newEmailController = loader.getController();
      newEmailController.initModel(this.model);
      newEmailController.setStage(stage);
      newEmailController.setSender(this.model.getUser().getUserName());
      return newEmailController;
    } catch (IOException e) {
      this.label_log.setText(LabelMessage.new_email_sceneLoading_error);
      removeLabelMessage(this.label_log, Duration.seconds(2));
      e.printStackTrace();
    }
    return null;
  }

  public void setLabelLog(Label label) {
    this.label_log = label;
  }

  private void removeLabelMessage(Label label, Duration d) {
    PauseTransition delayLog = new PauseTransition(d);
    final String css = LabelMessage.css_backgroundColor + ColorManager.defaultColor;
    delayLog.setOnFinished(e -> {
      label.setText("");
      label.setStyle(css);
    });
    delayLog.play();
  }
}

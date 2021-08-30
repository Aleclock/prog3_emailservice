package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lib.ColorManager;
import lib.EmailProperty;
import lib.LabelMessage;

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
  public void handleReplyAll(ActionEvent event) {
    NewEmailController controller = loadNewMailScene();
    if (controller != null) {
      String recipients = this.label_sender.textProperty().get() + " " + label_recipipients.textProperty().get();
      recipients = recipients.replace(this.model.getUser().getUserName(), "");
      recipients = recipients.replace(" , ", "");
      controller.setRecipients(recipients);
      controller.setSubject("RE: " + this.label_subject.textProperty().get());
    }
  }

  @FXML
  public void handleDeleteEmail (ActionEvent event) {
    try {
      EmailProperty emailProperty = this.model.getCurrentEmailSelected().get();
      String message = this.model.requestDeleteEmail(emailProperty.getUuid());

      String cssValue;
      if (message.contains("successfully")) {
        cssValue = "-fx-background-color: " + ColorManager.successColor;
        clearAll();
      } else {
        cssValue = "-fx-background-color: " + ColorManager.errorColor;
      }
      this.label_log.setStyle(cssValue);
      this.label_log.setText(message);
      // TODO if message contains "correctly" show banner, remove all texts
    } catch (IOException e) {
      // TODO handle error
    }
  }

  @FXML
  public void handleReply (ActionEvent event) {
    NewEmailController controller = loadNewMailScene();
    if (controller != null) {
      controller.setRecipients(this.label_sender.textProperty().get());
      controller.setSubject("RE: " + this.label_subject.textProperty().get());
    }
  }

  @FXML
  public void handleForward (ActionEvent event) {
    NewEmailController controller = loadNewMailScene();
    if (controller != null) {
      controller.setSubject(this.label_subject.textProperty().get());
      controller.setBody(this.label_body.textProperty().get());
    }
  }

  @FXML
  public void handleReadUnread(ActionEvent event) {
    EmailProperty emailProperty = this.model.getCurrentEmailSelected().get();
    boolean result = this.model.setEmailReadorNot(emailProperty.getUuid(), false);
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

      NewEmailController controller = loader.getController();
      controller.initModel(this.model);
      controller.setSender(this.model.getUser().getUserName());
      return controller;
    } catch (IOException e) {
      this.label_log.setText(LabelMessage.new_email_sceneLoading_error);
      e.printStackTrace();
    }
    return null;
  }

  public void setLabelLog(Label label) {
    this.label_log = label;
  }
}

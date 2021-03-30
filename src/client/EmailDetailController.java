package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class EmailDetailController {
  private Model model;
  @FXML
  private Label label_recipipients, label_subject, label_sender, label_date, label_body;

  public void initModel(Model model) {
    this.model = model;
    this.model.getCurrentEmailSelected().addListener( (obs, oldEmail, newEmail) -> {
      System.out.println(oldEmail + " , " + newEmail);
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
    // TODO gestire, inviare a tutti tranne a te (recipients + sender)
  }

  @FXML
  public void handleDeleteEmail (ActionEvent event) {
    NewEmailController controller = loadNewMailScene();
    // TODO gestire
  }

  @FXML
  public void handleReply (ActionEvent event) {
    NewEmailController controller = loadNewMailScene();
    controller.setRecipients(this.label_sender.textProperty().get());
    controller.setSubject ("RE: " + this.label_subject.textProperty().get());
  }

  @FXML
  public void handleForward (ActionEvent event) {
    NewEmailController controller = loadNewMailScene();
    controller.setSubject (this.label_subject.textProperty().get());
    controller.setBody(this.label_body.textProperty().get());
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
      controller.initModel(model);
      return controller;
    } catch (IOException e) {
      System.out.println("ERRORE nel caricamento di new_email_scene.fxml");
      e.printStackTrace();
    }
    return null;
  }
}

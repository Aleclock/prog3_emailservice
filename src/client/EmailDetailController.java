package client;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class EmailDetailController {
  private Model model;
  @FXML
  private Label label_recipipients, label_subject, label_sender, label_date, label_body;
  @FXML
  private Button btn_delete_email, btn_reply_all, btn_reply, forward;

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


}

package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Controller {
  Model model;

  @FXML
  private TextField tf_email;
  @FXML
  private Label label_error;

  public void initModel(Model model) {
    this.model = model;
  }

  @FXML
  public void handleLoginButton(ActionEvent event) {
    if (matchesEmailFormat(tf_email.getText())) {
      System.out.println("bravoo");
      // TODO open email page
      // TODO open connection with server
    } else {
      System.out.println("errore");
      label_error.setText("Please enter a valid email address");
    }
  }

  @FXML
  public void handleTextFieldSelection(MouseEvent event) {
    label_error.setText("");
  }

  private boolean matchesEmailFormat(String email) {
    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    return email.matches(regex);
  }
}

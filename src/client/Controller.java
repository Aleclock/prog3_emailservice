package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

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
    Connection conn = new Connection();
    conn.connect();
    if (matchesEmailFormat(tf_email.getText()) && conn.login(tf_email.getText())) {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("res/client_main_scene.fxml"));
        Parent root = loader.load();
        tf_email.getScene().setRoot(loader.getRoot());
        // TODO open connection with server
      } catch(IOException ex) {
        ex.printStackTrace();
      }
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

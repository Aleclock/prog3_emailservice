package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lib.User;

import java.io.IOException;
import java.net.SocketException;

public class Controller {
  private Model model;

  @FXML
  private TextField tf_email;
  @FXML
  private Label label_error;

  public void initModel(Model model) {
    this.model = model;
  }

  @FXML
  public void handleLoginButton(ActionEvent event) {
    model.setUser(new User(tf_email.getText()));
    Connection connection = model.getConnection();
    try {
      model.connectUser();
    } catch(SocketException ex) {
      Alert a = getDialog("Server non raggiungibile", "Server non raggiungibile, riprova più tardi.");
      a.showAndWait();
    }

    if (matchesEmailFormat(tf_email.getText()) && connection.login(tf_email.getText())) {
      try {
        connection.setUser(model.getUser()); // TODO capire se ha senso e se metterlo qua
        FXMLLoader loader = new FXMLLoader(getClass().getResource("res/client_main_scene.fxml"));
        Parent root = loader.load();
        tf_email.getScene().setRoot(loader.getRoot());

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setTitle(this.model.getUser().getUserName());
        // TODO cliccare "aggiorna" in modo da ottenere le mail
        Button refresh = (Button) loader.getNamespace().get("refresh");
        refresh.fire();
        //TextField foo = (TextField)loader.getNamespace().get("exampleFxId");
      } catch(IOException ex) {
        ex.printStackTrace();
      }
    } else {
      // TODO questo è un errore generico, magari differenziare
      label_error.setText("Please enter a valid email address");
    }
  }

  @FXML
  public void handleTextFieldSelection(MouseEvent event) {
    label_error.setText("");
  }

  @FXML
  public void handleRefreshButton(ActionEvent event) {
    System.out.println("Mò aggiorno");
  }

  @FXML
  public void handleNewMailButton(ActionEvent event) {
  }

  private boolean matchesEmailFormat(String email) {
    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    return email.matches(regex);
  }

  private Alert getDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle(title);
    alert.setContentText(message);
    Stage nStage = (Stage) alert.getDialogPane().getScene().getWindow();
    return alert;
  }

  public Model getModel() {
    return this.model;
  }
}

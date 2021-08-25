package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lib.User;
import java.io.IOException;
import java.net.SocketException;

public class LoginController {
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
    // TODO perchè lo faccio qui e non solo nel caso in cui la mail sia valida?
    this.model.setUser(new User(tf_email.getText()));
    Connection connection = model.getConnection();
    try {
      this.model.connectUser();
    } catch(SocketException ex) {
      // TODO creare classe di testi/messaggi
      Alert a = getDialog("Server non raggiungibile", "Server non raggiungibile, riprova più tardi.");
      a.showAndWait();
    }

    if (this.model.matchesEmailFormat(tf_email.getText()) && connection.login(tf_email.getText())) {
      try {
        connection.setUser(this.model.getUser()); // TODO capire se ha senso e se metterlo qua
        FXMLLoader loader = new FXMLLoader(getClass().getResource("res/client_main_scene.fxml"));
        BorderPane root = loader.load();
        MainSceneController controller = loader.getController();
        tf_email.getScene().setRoot(loader.getRoot());

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setTitle(this.model.getUser().getUserName());
        stage.setWidth(1200);
        stage.setHeight(450);
        //stage.setMinWidth();
        //stage.setMinHeight(root.getScene().getHeight());

        controller.initModel(this.model);
        controller.initalize();

        System.out.println("User :" + this.model.getUser().getUserName());
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

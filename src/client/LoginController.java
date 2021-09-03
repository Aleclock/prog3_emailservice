package client;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lib.ColorManager;
import lib.LabelMessage;
import lib.User;
import java.io.IOException;

public class LoginController {
  private Model model;
  private MainSceneController mainSceneController;

  @FXML
  private TextField tf_email;
  @FXML
  private Label label_error, label_log;

  public void initModel(Model model) {
    this.model = model;
  }

  @FXML
  public void handleLoginButton() {
    Connection connection = model.getConnection();
    boolean result = this.model.connectUser();
    if (!result) {
      this.label_log.setText(LabelMessage.serverUnreachableLabel);
      this.label_log.setStyle(LabelMessage.css_backgroundColor + ColorManager.errorColor);
      removeLabelMessage(this.label_log, Duration.seconds(4));
    } else {

      if (this.model.matchesEmailFormat(tf_email.getText()) && connection.login(tf_email.getText()).getResult()) {
        try {
          this.model.setUser(new User(tf_email.getText()));
          connection.setUser(this.model.getUser());
          FXMLLoader loader = new FXMLLoader(getClass().getResource("res/client_main_scene.fxml"));
          BorderPane root = loader.load();
          this.mainSceneController = loader.getController();
          tf_email.getScene().setRoot(loader.getRoot());

          Stage stage = (Stage) root.getScene().getWindow();
          stage.setTitle(this.model.getUser().getUserName());
          stage.setWidth(1200);
          stage.setHeight(500);

          this.mainSceneController.initModel(this.model);
          this.mainSceneController.initalize();

          System.out.println("User :" + this.model.getUser().getUserName());
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      } else {
        this.label_log.setText(LabelMessage.loginFailed);
        this.label_log.setStyle(LabelMessage.css_backgroundColor + ColorManager.errorColor);
        removeLabelMessage(this.label_log, Duration.seconds(4));
      }
    }
  }

  @FXML
  public void handleTextFieldSelection() {
    label_error.setText("");
  }

  private void removeLabelMessage(Label label, Duration d) {
    PauseTransition delayLog = new PauseTransition(d);
    delayLog.setOnFinished(e -> {
      label.setText("");
      label.setStyle(null);
    });
    delayLog.play();
  }

  public Model getModel() {
    return this.model;
  }

  public MainSceneController getMainSceneController() {
    return this.mainSceneController;
  }
}

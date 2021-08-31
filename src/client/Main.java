package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  LoginController loginController;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("res/login_scene.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

    this.loginController = loader.getController();
    this.loginController.initModel(new Model());
  }

  @Override
  public void stop()  {
    MainSceneController mainSceneController = this.loginController.getMainSceneController();

    if (mainSceneController != null) {
      mainSceneController.removeConnectionListener();
    }

    this.loginController.getModel().logout();
  }

  public static void main (String[] args) {
    launch(args);
  }
}

package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  Controller controller;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("res/login_scene.fxml"));
    Parent root = loader.load();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

    controller = loader.getController();
    controller.initModel(new Model());
  }

  @Override
  public void stop() throws Exception {
    //controller.removeListener();
    controller.getModel().closeConnection();
  }

  public static void main (String[] args) {
    launch(args);
  }
}

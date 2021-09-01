package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.PrintStream;

public class Main extends Application {
  Controller controller;

  @Override
  public void start(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("server_scene.fxml"));
      Parent root = loader.load();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.show();

      controller = loader.getController();
      PrintStream ps = controller.getPrintStream();
      Server server = new Server(ps);
      controller.setServer(server);
      controller.bindOpenConnection(Model.getOpenConnectionProperty());
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  @Override
  public void stop() throws Exception {
    controller.pauseServer();
    super.stop();
  }

  public static void main (String[] args) {
    launch(args);
  }
}

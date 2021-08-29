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

    loginController = loader.getController();
    loginController.initModel(new Model());
  }

  @Override
  public void stop()  {
    //controller.removeListener(); TODO capire che ascoltatori rimuovere (gli ascoltatori vengono settati nel MainSceneController e non so quando rimuoverli)
    loginController.getModel().closeConnection();
    // TODO capire se la connessione con il server viene chiusa ogni volta (ogni volta viene aperta e chiusa la connessione) oppure no
  }

  public static void main (String[] args) {
    launch(args);
  }
}

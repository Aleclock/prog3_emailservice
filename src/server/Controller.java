package server;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import lib.ColorManager;

import java.io.OutputStream;
import java.io.PrintStream;

public class Controller {
  private PrintStream ps;
  private Server server;
  @FXML
  private TextArea console;
  @FXML
  private ToggleButton toggle_server_status;

  // This method is called automatically after the constructor and any @FXML field (https://stackoverflow.com/questions/51392203/what-does-initialize-mean-in-javafx)
  public void initialize() {
    this.ps = new PrintStream(new Console(console), true);
    System.setErr(ps);

    this.toggle_server_status.setText("ON");
    this.toggle_server_status.setStyle("-fx-background-color: " + ColorManager.successColor);
  }

  @FXML
  private void handleServerStatus(Event event){
    String cssValue;
    String message;
    if (toggle_server_status.isSelected()) {
      cssValue = "-fx-background-color: " + ColorManager.successColor;
      message = "ON";
      startServer();
    } else {
      cssValue = "-fx-background-color: " + ColorManager.errorColor;
      message = "OFF";
      pauseServer();
    }
    this.toggle_server_status.setStyle(cssValue);
    this.toggle_server_status.setText(message);
  }

  @FXML
  private void handleExecutorStatus() {
    this.server.isExecutorTermined();
  }

  public void startServer() {
    this.server.startServer();
  }

  public void pauseServer() {
    this.server.stopServer();
  }

  public PrintStream getPrintStream() {
    return this.ps;
  }


  public void setServer(Server server) {
    this.server = server;
  }


  /**
   * This class allow to log and write messages on console TextArea
   * **/
  public static class Console extends OutputStream {
    private final TextArea console;

    Console(TextArea console) {
      this.console = console;
    }

    void appendText(String valueOf) {
      Platform.runLater(() -> console.appendText(valueOf));
    }

    public void write(int b) {
      appendText(String.valueOf((char)b));
    }
  }
}

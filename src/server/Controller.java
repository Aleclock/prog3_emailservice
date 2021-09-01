package server;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import lib.ColorManager;
import lib.LabelMessage;

import java.io.OutputStream;
import java.io.PrintStream;

public class Controller {
  @FXML
  private TextArea console;
  @FXML
  private ToggleButton toggle_server_status;
  @FXML
  private Label label_connectedUser;

  private PrintStream ps;
  private Server server;
  private StringProperty openConnectionProperty;

  // This method is called automatically after the constructor and any @FXML field (https://stackoverflow.com/questions/51392203/what-does-initialize-mean-in-javafx)
  public void initialize() {
    this.ps = new PrintStream(new Console(console), true);
    System.setErr(ps);

    openConnectionProperty = label_connectedUser.textProperty();
    this.toggle_server_status.setText("ON");
    this.toggle_server_status.setStyle(LabelMessage.css_backgroundColor + ColorManager.successColor);
  }

  public void bindOpenConnection(StringProperty stringProperty) {
    this.openConnectionProperty.bind(stringProperty);
  }

  @FXML
  private void handleServerStatus(Event event){
    String cssValue;
    String message;
    if (toggle_server_status.isSelected()) {
      cssValue = LabelMessage.css_backgroundColor + ColorManager.successColor;
      message = "ON";
      startServer();
    } else {
      cssValue = LabelMessage.css_backgroundColor + ColorManager.errorColor;
      message = "OFF";
      pauseServer();
    }
    this.toggle_server_status.setStyle(cssValue);
    this.toggle_server_status.setText(message);
  }

  @FXML
  private void handleClearConsole() {
    this.console.clear();
  }

  public void startServer() {
    this.server.startServer();
    //label_connectedUser.textProperty().bind(server.getConnectedUserProperty());
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

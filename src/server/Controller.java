package server;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import java.io.OutputStream;
import java.io.PrintStream;

public class Controller {
  private PrintStream ps;
  private Server server;
  @FXML
  private TextArea console;
  @FXML
  private ToggleButton toggle_server_status;

  public void initialize() {
    this.ps = new PrintStream(new Console(console), true);
    //System.setOut(ps);
    System.setErr(ps);
  }

  @FXML
  private void handleServerStatus(Event event){
    if (toggle_server_status.isSelected()) {
      start();
    } else {
      pause();
    }
  }

  private void start() {
    this.server.startServer();
  }

  private void pause() {
    this.server.stopServer();
  }

  public PrintStream getPrintStream() {
    return this.ps;
  }


  public void setServer(Server server) {
    this.server = server;
  }

  public static class Console extends OutputStream {
    private TextArea console;

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

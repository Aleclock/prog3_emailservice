package server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.Console;
import java.io.OutputStream;
import java.io.PrintStream;

public class Controller {
  private PrintStream ps;
  private Server server;
  @FXML
  private TextArea console;
  @FXML


  public void initialize() {
    this.ps = new PrintStream(new Console(console), true);
    //System.setOut(ps);
    System.setErr(ps);
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

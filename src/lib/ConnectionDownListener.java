package lib;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

public class ConnectionDownListener<B extends Boolean> implements ChangeListener<B> {
  private Label label_log;

  @Override
  public void changed(ObservableValue<? extends B> observableValue, B b, B t1) {
    boolean isDown = !(Boolean) observableValue.getValue();
    String cssValue;

    String message;
    if (isDown) {
      message = LabelMessage.serverDown;
      cssValue = "-fx-background-color: " + ColorManager.errorColor;
    } else {
      message = LabelMessage.serverWorking;
      cssValue = "-fx-background-color: " + ColorManager.successColor;
    }

    this.label_log.setStyle(cssValue);
    this.label_log.setText(message);
  }

  public void setLabelLog(Label l) {
    this.label_log = l;
  }
}

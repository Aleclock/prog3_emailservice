package lib;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class ConnectionDownListener<B extends Boolean> implements ChangeListener<B> {
  private Label label_log;

  @Override
  public void changed(ObservableValue<? extends B> observableValue, B b, B t1) {
    boolean isDown = !(Boolean) observableValue.getValue();
    String cssValue;

    String message;
    if (isDown) {
      message = LabelMessage.serverDown;
      cssValue = LabelMessage.css_backgroundColor + ColorManager.errorColor;
    } else {
      message = LabelMessage.serverWorking;
      cssValue = LabelMessage.css_backgroundColor + ColorManager.successColor;
      removeLabelMessage(this.label_log, Duration.seconds(2));
    }

    this.label_log.setStyle(cssValue);
    this.label_log.setText(message);
  }

  public void setLabelLog(Label l) {
    this.label_log = l;
  }

  private void removeLabelMessage(Label label, Duration d) {
    PauseTransition delayLog = new PauseTransition(d);
    delayLog.setOnFinished(e -> {
      label.setText("");
      label.setStyle(null);
    });
    delayLog.play();
  }
}

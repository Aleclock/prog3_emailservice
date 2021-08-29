package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lib.ConnectionDownListener;
import lib.Email;
import lib.EmailProperty;
import java.io.IOException;

public class MainSceneController {
  private Model model;
  private boolean listAlreadySelected = false;
  private ConnectionDownListener<Boolean> connectionDownListener;

  @FXML
  private Button refresh;
  @FXML
  private ListView lv_emails;
  @FXML
  private Pane pane_email_detail;
  @FXML
  private Label label_log;

  public void initalize() {
    initListView(lv_emails, pane_email_detail);
    initConnectionListener();
    this.model.refreshEmailList();
    refresh.fire();
  }

  public void initModel(Model model) {
    this.model = model;
  }

  @FXML
  public void handleRefreshButton(ActionEvent event) {
    this.model.retrieveEmails();
  }

  @FXML
  public void handleNewMailButton(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("res/new_email_scene.fxml"));
      Scene scene = new Scene(loader.load());
      Stage stage = new Stage();
      stage.setTitle(this.model.getUser().getUserName() + " - New email");
      stage.setScene(scene);
      stage.show();

      NewEmailController newEmailController = loader.getController();
      newEmailController.initModel(model);
      newEmailController.setSender(this.model.getUser().getUserName());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initConnectionListener() {
    this.connectionDownListener = new ConnectionDownListener<Boolean>();
    this.connectionDownListener.setLabelLog(this.label_log);
    this.model.addListenerToConnectionStatus(this.connectionDownListener);
  }

  private void initListView(ListView lv_emails, Pane controller) {
    lv_emails.setItems(this.model.getEmailReceived());

    lv_emails.setCellFactory(new Callback<ListView<Email>, ListCell<Email>>() {
        @Override
        public ListCell<Email> call(ListView<Email> emailListView) {
          return new CustomListViewItem();
        }
    });

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("res/email_detail_scene.fxml"));
      Parent root = loader.load();
      Scene scene = new Scene(root);
      EmailDetailController emailDetailController = loader.getController();
      emailDetailController.initModel(this.model);
      emailDetailController.setLabelLog(this.label_log);

      this.pane_email_detail.getChildren().addAll(scene.getRoot());
      this.pane_email_detail.setVisible(false);
    } catch (IOException e) {
      e.printStackTrace();
    }

    lv_emails.getSelectionModel().selectedItemProperty().addListener( (obs, oldSelection, newSelection) -> {
      if (!this.listAlreadySelected) {
        this.pane_email_detail.setVisible(true);
        this.listAlreadySelected = true;
      }
      if (newSelection != null) {
        Email email = (Email) newSelection;
        this.model.setCurrentEmail(new EmailProperty(email));
        if (!email.hasBeenRead()) {
          this.model.setEmailReadorNot(email, true);
        }
      }
    });
  }

  @FXML
  private void setEmailReceived (ActionEvent event) {
    this.lv_emails.setItems(this.model.getEmailReceived());
  }

  @FXML
  private void setEmailSent (ActionEvent event) {
    this.lv_emails.setItems(this.model.getEmailsSent());

  }
}

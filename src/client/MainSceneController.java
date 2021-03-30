package client;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lib.Email;
import lib.EmailProperty;
import java.io.IOException;

public class MainSceneController {
  private Model model;
  private boolean listAlreadySelected = false;

  @FXML
  private Button refresh;
  @FXML
  private ListView lv_emails;
  @FXML
  private Pane pane_email_detail;

  public void initalize() {
    initListView(lv_emails, pane_email_detail);

    // TODO aggiungere listener relativi alla connessione (se il server è down o qualcosa del genere)
    // TODO aggiungere listener alla lista di email in modo tale che si aggiorni in automatico
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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initListView(ListView lv_emails, Pane controller) {
    ObservableList<Email> emailList = this.model.getEmails();
    System.out.println(lv_emails);
    lv_emails.setItems(emailList);

    lv_emails.setCellFactory(new Callback<ListView<Email>, ListCell<Email>>() {
        @Override
        public ListCell<Email> call(ListView<Email> emailListView) {
          return new CustomListViewItem();
        }
    });

    // TODO al momento quando viene inizializzata la lista viene aggiunta la schermata della mail e settata come non visibile
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("res/email_detail_scene.fxml"));
      Parent root = loader.load();
      Scene scene = new Scene(root);
      EmailDetailController emailDetailController = loader.getController();
      emailDetailController.initModel(this.model);

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
      Email email = (Email) newSelection;
      this.model.setCurrentEmail(new EmailProperty(email));
      // TODO set email selected
    });
  }
}
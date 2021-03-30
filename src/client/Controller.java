package client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lib.Email;
import lib.EmailProperty;
import lib.User;

import java.io.IOException;
import java.net.SocketException;

public class Controller {
  private Model model;
  private boolean listAlreadySelected = false;

  @FXML
  private TextField tf_email;
  @FXML
  private Label label_error;
  @FXML
  private TextField tf_recipients, tf_subject;
  @FXML
  private TextArea ta_body;
  @FXML
  private Pane pane_email_detail;
  @FXML
  private Label label_recipipients, label_subject, label_sender, label_date, label_body;

  private BorderPane borderPane_root;


  public void initModel(Model model) {
    this.model = model;
  }

  @FXML
  public void handleLoginButton(ActionEvent event) {
    this.model.setUser(new User(tf_email.getText()));
    Connection connection = model.getConnection();
    try {
      this.model.connectUser();
    } catch(SocketException ex) {
      Alert a = getDialog("Server non raggiungibile", "Server non raggiungibile, riprova più tardi.");
      a.showAndWait();
    }

    if (matchesEmailFormat(tf_email.getText()) && connection.login(tf_email.getText())) {
      try {
        connection.setUser(this.model.getUser()); // TODO capire se ha senso e se metterlo qua
        FXMLLoader loader = new FXMLLoader(getClass().getResource("res/client_main_scene.fxml"));
        BorderPane root = loader.load();
        Controller controller = loader.getController();

        tf_email.getScene().setRoot(loader.getRoot());
        this.borderPane_root = loader.getRoot();
        Pane emailPane = (Pane) loader.getNamespace().get("pane_email_detail");
        this.pane_email_detail = emailPane;
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setTitle(this.model.getUser().getUserName());

        // TODO ridimensionare finestra
        Button refresh = (Button) loader.getNamespace().get("refresh");
        ListView lv_emails = (ListView) loader.getNamespace().get("lv_emails");
        controller.initModel(this.model);
        initListView(lv_emails, emailPane);

        // TODO aggiungere listener relativi alla connessione (se il server è down o qualcosa del genere)
        // TODO aggiungere listener alla lista di email in modo tale che si aggiorni in automatico
        this.model.refreshEmailList();
        refresh.fire();
      } catch(IOException ex) {
        ex.printStackTrace();
      }
    } else {
      // TODO questo è un errore generico, magari differenziare
      label_error.setText("Please enter a valid email address");
    }
  }

  @FXML
  public void handleTextFieldSelection(MouseEvent event) {
    label_error.setText("");
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

  private boolean matchesEmailFormat(String email) {
    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    return email.matches(regex);
  }

  private Alert getDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle(title);
    alert.setContentText(message);
    Stage nStage = (Stage) alert.getDialogPane().getScene().getWindow();
    return alert;
  }

  public Model getModel() {
    return this.model;
  }
}

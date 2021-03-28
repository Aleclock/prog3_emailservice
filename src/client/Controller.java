package client;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import lib.Email;
import lib.User;

import java.io.IOException;
import java.net.SocketException;

public class Controller {
  private Model model;

  @FXML
  private TextField tf_email;
  @FXML
  private Label label_error;
  @FXML
  private TextField tf_recipients, tf_subject;
  @FXML
  private TextArea ta_body;

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
        Parent root = loader.load();
        tf_email.getScene().setRoot(loader.getRoot());
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setTitle(this.model.getUser().getUserName());
        Button refresh = (Button) loader.getNamespace().get("refresh");
        ListView lv_emails = (ListView) loader.getNamespace().get("lv_emails");
        Controller controller = loader.getController();
        controller.initModel(this.model);
        initListView(lv_emails);
        // TODO aggiungere listener relativi alla connessione (se il server è down o qualcosa del genere)
        // TODO aggiungere listener alla lista di email in modo tale che si aggiorni in automatico
        this.model.refreshEmailList();
        refresh.fire();
        //TextField foo = (TextField)loader.getNamespace().get("exampleFxId");
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
    Parent root;
    try {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("res/new_email_scene.fxml"));
    Scene scene = new Scene(loader.load());
    Stage stage = new Stage();
    stage.setTitle(this.model.getUser().getUserName() + " - New email");
    stage.setScene(scene);
    stage.show();

    Controller cont = loader.getController();
    cont.initModel(model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void handleSendEmail(ActionEvent event) {
    String body = ta_body.getText();
    String recipients = tf_recipients.getText();
    String subject = tf_subject.getText();
    if (subject.equals("")) {
      subject = "(no subject)";
    }

    try {
      String message = this.model.requestSendMail(recipients, subject, body);
      // TODO stampare messaggio da qualche parte
      // TODO valutare se aggiungere direttamente la mail alla lista se tutto è andato bene
    } catch(IOException e) {
      // TODO gestire errori e messaggi
    }
  }

  private void initListView(ListView lv_emails) {
    ObservableList<Email> emailList = this.model.getEmails();
    System.out.println(lv_emails);
    lv_emails.setItems(emailList);
    lv_emails.setCellFactory(new Callback<ListView<Email>, ListCell<Email>>() {
        @Override
        public ListCell<Email> call(ListView<Email> emailListView) {
          return new CustomListViewItem();
        }
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

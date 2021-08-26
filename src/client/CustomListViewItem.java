package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import lib.Email;

public class CustomListViewItem extends ListCell<Email> {
  private static final String filePath = "res/email_list_item.fxml";
  private AnchorPane item;
  private Label subject;
  private Label recipients;
  private Label body_preview;
  private Label date;
  private Circle status;

  public CustomListViewItem() {
    super();
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(filePath));
      item = loader.load();

      this.subject = (Label) item.lookup("#label_subject");
      this.recipients = (Label) item.lookup("#label_recipients");
      this.date = (Label) item.lookup("#label_date");
      this.body_preview = (Label) item.lookup("#label_body_preview");
      this.status = (Circle) item.lookup("#circle_status");
    } catch (Exception e){
      System.out.println("Errore nel caricamento di " + filePath);
    }
  }

  @Override
  protected void updateItem(Email email, boolean empty) {
    super.updateItem(email, empty);
    if (email != null && !empty) {
      this.subject.setText(email.getSubject());
      this.recipients.setText(email.recipientsAsString());
      this.date.setText(email.getFormattedDate());
      this.body_preview.setText(email.getBody());
      this.status.setVisible(!email.hasBeenRead());
      setGraphic(item);
    } else {
      setGraphic(null);
    }
  }
}

package lib;

public class LabelMessage {

  public static String css_backgroundColor = "-fx-background-color: ";

  public static String operationSuccess = "Operazione riuscita";
  public static String operationFailed = "Operazione fallita";
  public static String serverUnreachableTitle = "Server non raggiungibile";
  public static String serverUnreachableLabel = "Server non raggiungibile, riprova più tardi.";
  public static String loginFailed = "Login fallito: inserire un indirizzo valido o riprovare più tardi.";

  public static String emailSentSuccess = "Email successfully sent";
  public static String emailSentError = "Error: email not sent";

  public static String emailDeleteSuccess = "Email successfully deleted";
  public static String emailDeleteError = "Error: email not deleted";

  public static String serverDown = "Server non raggiungibile, riprova più tardi.";
  public static String serverWorking = "Server raggiungibile, puoi tornare ad usare il programma.";

  public static String new_email_sceneLoading_error = "Errore nel caricamento di new_email_scene.fxml";

  public static String client_sendEmail_emailNotValid_error = "Recipient's email not valid";
  public static String client_sendEmail_noRecipient_error = "Errore: inserire almeno un destinatario";

  // SERVER

  public static String error_invalidSocket = "ERROR: Invalid socket";
  public static String serverClosingSocket = "Server: chiusura socket";
  public static String serverClosingSocketError = "Server: errore nella chiusura della socket";

  public static String connectionClosed = "connection closed";

  public static String serverOpen = "Server status: aperto";
  public static String serverClosed = "Server status: chiuso";
  public static String serverTermined = "Server: server terminato";
  public static String serverNotTermined = "Server: server ancora in esecuzione";
  public static String retryClosingServer = "Server: nuovo tentativo di chiusura";
  public static String suspendedRequestAcceptance = "Server: Sospesa accettazione di nuove richieste";

  public static String socketTimout = "Server: socket timeout";
  public static String startedNewProcess = "Server: avviato nuovo processo.";

  public static String server_userLogin = "logged in";
  public static String server_userLogout = "logged out";
  public static String server_userLogin_noExist_error = "user not exist";
  public static String server_userLogin_alreadyLogged_error = "user already logged in";

  public static String server_readEmails_success = "lettura della casella email andata a buon fine.";
  public static String server_readEmails_error = "lettura della casella email fallita";
  public static String server_sendEmail_success = "email sent successfully.";
  public static String server_sendEmail_error = "sending mail failed";
  public static String server_sendEmail_userNotExist = "not exist: Sending mail failed\n";
  public static String server_deleteEmail_success = "successfully deleted";
  public static String server_deleteEmail_error = "ERROR while deleting email";

}

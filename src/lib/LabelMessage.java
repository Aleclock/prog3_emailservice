package lib;

public class LabelMessage {

  public static String css_backgroundColor = "-fx-background-color: ";
  public static String css_buttonSelected = "-fx-background-color: #CECBCA; -fx-border-color: #CECBCA; -fx-border-radius: 7; -fx-background-radius: 7; -fx-border-width: 1;";
  public static String css_buttonNoSelected = "-fx-background-color: none;";

  public static String operationSuccess = "Operation completed successfully";
  public static String operationFailed = "Operation failed";
  public static String serverUnreachableTitle = "Server unreachable";
  public static String serverUnreachableLabel = "Server unreachable, try again later.";
  public static String loginFailed = "Login failed: enter a valid address or try again later.";

  public static String client_refreshEmail_success = "";
  public static String client_refreshEmail_error = "ERROR: refreshing email";

  public static String emailSentSuccess = "Email successfully sent";
  public static String emailSentError = "Error: email not sent";

  public static String emailDeleteSuccess = "Email successfully deleted";
  public static String emailDeleteError = "Error: email not deleted";

  public static String serverDown = "Server unreachable, try again later.";
  public static String serverWorking = "Server reachable again, you can go back to using the program.";

  public static String new_email_sceneLoading_error = "Loading error of new_email_scene.fxml";

  public static String client_sendEmail_emailNotValid_error = "Recipient's email not valid";
  public static String client_sendEmail_noRecipient_error = "Error: please insert at least one recipient";
  public static String client_sendEmail_success = "Email sent successfully.";
  public static String client_sendEmail_error = "ERROR: sending email failed";

  public static String client_emailSetRead_success = "Email set as read";
  public static String client_emailSetRead_error = "ERROR: impossible setting email as read";
  public static String client_emailSetUnread_success = "Email set as unread";
  public static String client_emailSetUnread_error = "ERROR: impossible setting email as unread";

  // SERVER

  public static String error_invalidSocket = "ERROR: Invalid socket";
  public static String serverClosingSocket = "Server: closing socket";
  public static String serverClosingSocketError = "Server: error during closing the socket";

  public static String connectionClosed = "connection closed";

  public static String serverOpen = "Server status: open";
  public static String serverClosed = "Server status: closed";
  public static String serverNotTermined = "Server: server still running";
  public static String retryClosingServer = "Server: new closure attempt";
  public static String suspendedRequestAcceptance = "Server: acceptance of new requests suspended";

  public static String socketTimout = "Server: socket timeout";
  public static String startedNewProcess = "Server: new process started.";

  public static String server_userLogin = "logged in";
  public static String server_userLogout = "logged out";
  public static String server_userLogin_noExist_error = "user not exist";
  public static String server_userLogin_alreadyLogged_error = "user already logged in";

  public static String server_readEmails_success = "reading email box operation completed successfully";
  public static String server_readEmails_error = "reading email box operation failed";
  public static String server_sendEmail_success = "email sent successfully.";
  public static String server_sendEmail_error = "sending email failed";
  public static String server_sendEmail_userNotExist = "not exist: Sending mail failed\n";
  public static String server_deleteEmail_success = "successfully deleted";
  public static String server_deleteEmail_error = "ERROR while deleting email";
  public static String server_getOrCreateEmailBox_success = "Email box retrieved correctly";
  public static String server_getOrCreateEmailBox_error = "ERROR: Email box not retrieved correctly";

  public static String server_errorEmail_subject = "Undelivered Mail Returned to Sender";
  public static String server_errorEmail_body = "This is a system-generated message to inform you that your email could not be delivered to following recipients. Details of the email and the error are as follows:\n";
}

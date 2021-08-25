# Prog3

## Inizializzazione
Per prima cosa, appena scaricato il progetto e dopo averlo aperto, è necessario aggiungere la librario javafx (https://openjfx.io/index.html) e gson (di Google). Inoltre, nel caso in cui non trovi le varie classi, fare File -> Invalidate caches -> Invalidate and restart. Successivamente buildare il progetto ed eseguire le classi `Main` dei package *client* e *server*.

## Package lib

### User

La classe `User` definisce l'utente che utilizza la casella mail. La classe implementa la classe `Serializable` per permettere il passaggio di informazioni tra socket (TODO verificare sia vero). `User` possiede solo il campo `userName` e fa l'override della funzione `equals`, la quale verifica se due oggetti di tipo `User` sono uguali in base al valore del campo `userName`.

### Email

La classe `Email` definisce la struttura di un'email e possiede i seguenti campi:
- `uuid`, l'id della mail (`long`);
- `sender`, il mittente della mail (`User`);
- `recipients`, la lista dei destinatari (`List<User>`);
- `dataSent`, data di invio della mail (`Date`);
- `subject`, oggetto della mail (`String`);
- `body`, corpo delle mail (`String`);
- `read`, indica se la mail è stata letta o no (`bool`).

Un oggetto di tipo `Email` può essere inizializzato impostato come destinatario sia una lista di utenti (lsta di oggetti `User`) sia un singolo destinatario (oggetto `User`). 
<br>
TODO Al momento l'id dell'email è formato dalla somma degli hash code di tutti i campi della singola email.

La classe `Email` effettua l'override del metodo equals in modo tale che due email siano uguali sono se sono uguali i loro `uuid`.

### EmailBox

L'`EmailBox` è la classe che definisce la casella email di un singolo utente, nel quale sono memorizzate tutte le sue email inviate e ricevute. La classe è composta da due campi:
- `user`, l'utente della casella email (`User`);
- `emailList`, lista di email inviate e ricevute (`List<Email>`).

La classe definisce i metodi:
- `addEmails`, aggiunge una o più email in cima (posizione 0) alla lista delle email dell'`EmailBox`;

### Command
La classe `Command` definisce il tipo di comando che permette la comunicazione tra client e server. La classe è composta da i seguenti campi:
- `user`, utente che sta inviando il comando al server (`User`);
- `command`, tipo di richesta che viene fatta al server (`String`);
- `emails`, TODO.

### ColorManager

La classe `ColorManager` viene utilzzata solamente per ritornare all'interfaccia grafica dei colori in base al tipo di azione effettuata (*success*, *error*).

### EmailProperty

TODO

## Package client

### Main

La classe `Main` fa l'override dei metodi `start` e `stop` dalla classe `Application` di javafx. il metodo `start` carica la scena di login e la apre attraverso il metodo `stage.show()`. Infine viene caricato il controller relativo alla scena di login e viene inizializzato il `model`. La creazione di una nuova istanza di tipo `Model` permette, tra le altre cose, di creare un nuovo oggetto di tipo `Connection`, classe che si occupa della gestione della connessione tra client e server.

```java
loginController = loader.getController();
loginController.initModel(new Model());
```

Il metodo `stop` effettua la chiusura della connessione attraverson il metodo `closeConnection()`.
<br><br>

### LoginController

La classe `LoginController` è il controller del modello Model-View-Controller e si occupa di gestire tutte le operazioni che avvengono nell'interfaccia Login.

Il metodo `handleLoginButton` viene invocato quando il pulsante *Login* viene premuto. Questa funzione imposta l'utente nel *model*, connette l'utente con il server attraverso il metodo `connectUser()` e verifica se l'utente è presente nella lista di utenti nel server (metodo `login()`).

Se la mail inserita matcha con il formato standard di un email e l'utente risulta essere registrato (nome utente presente nella lista), allora viene cambiata la scena e viene aperta quella *client_main_scene* e viene inizializzato il nuovo controller (`MainSceneController`).

### MainSceneController

L'oggetto di tipo `MainSceneController` viene inizializzato nel momento in cui l'operazione di login avviene con successo. Subito dopo viene inizializzato il controller (`initalize()`), in particolare inizializzando il componente *listview* della vista (`initListView()`) e attiva un thread di aggiornamento della lista di email (`refreshEmailList()`). Infine il metodo `initalize()` esegue il metodo `handleRefreshButton()` attraverso la pressione del pulsante di refresh (`refresh.fire()`).

Il metodo `initListView` inizializza la componente *listview* della schermata, impostando la lista da visualizzare (`setItems()`) e impostando il formato di ogni elemento della lista. Quest'ultima operazione viene effettuata dalla classe `CustomListViewItem` la quale estende la classe `ListCell<Email>` la quale carica, per ogni cella della lista, la scena *email_list_item* e ottiene tutti i suoi elementi. Inoltre la classe effettua l'override del metodo `updateItem()`, in modo tale da poter aggiornare i valori degli elementi della cella con i valori della email da visualizzare. <br>
Il metodo `initListView` carica inoltre la scena *email_detail_scene* all'interno della scena principale, in modo tale da visualizzare in maniera completa le email selezionate nella listview. In particolare viene inizilizzato l'oggetto di tipo `EmailDetailController` e viene aggiunto un ascoltatore sugli elementi della `ListView` in modo tale che, se premuti, venga visualizzata interamente l'email nella schermata *email_detail_scene*.

```java
lv_emails.getSelectionModel().selectedItemProperty().addListener( (obs, oldSelection, newSelection) -> {
  // TODO capire a cosa serve
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
```

`setCurrentEmail()` è il metodo che imposta l'oggetto (tipo `EmailProperty`) su cui la classe `EmailDetailController` fa il binding. Nel caso in cui l'email selezionata non sia stata ancora letta, viene impostata come letta (`setEmailReadorNot()`).

La scena principale delle email prevede dei pulsanti che permettono il refresh delle email (fa richiesta al server delle email) e la scrittura di una nuova mail. Nel primo caso viene invocato il metodo `retrieveEmails()` della classe `Model`, mentre il metodo `handleNewMailButton()` apre una nuova finestra *new_email_scene*, inizializza il *model* nel controller specifico *NewEmailController* e imposta il mittente (ovvero l'utente loggato).

### EmailDetailController

Questa classe controlla tutte le operazioni che è possibile effettuare nella scena di dettaglio dell'email (*email_detail_scene*). Quando il controller viene inizializzato (`initModel`) vengono impostati i valori dell'email selezionata. Inoltre sono presenti diversi pulsanti che permettono di effettuare le seguenti operazioni:

- `handleReplyAll`, permette di rispondere a tutti gli utenti che fanno parte del mittente e del destinatario (escluso l'utente);
- `handleDeleteEmail`, permette la rimozione dell'email selezionata;
- `handleReply`, permette di rispondere solamente al mittente dell'email;
- `handleForward` permette di inoltrare l'email selezionata ad un altro utente.

I metodi `handleReply()` e `handleReplyAll()` caricano inizialmente la scena *new_email_scene*. Nel caso in cui il controller non sia nullo, viene settato il destinatario della risposta (o i destinatari) e viene impostato l'oggetto della mail aggiungendo "RE: " all'oggetto della mail precedente. Nel caso di risposta a più destinatari, la lista di destinatari è composta dal mittente più l'insieme dei destinatari escluso l'utente.

Il metodo `handleForward()` è simile alla risposta. In questo caso viene caricata la scena *new_email_scene* e, se il controller non è nullo, viene impostato l'oggetto e il corpo della mail (uguali ai valori della mail precedente).

Il metodo `handleDeleteEmail()` ottiene la mail da eliminare e invoca il metodo `requestDeleteEmail()` del *model*. In base all'esito dell'operazione viene mostrato un messaggio di errore o di successo.

### NewEmailController

Questo controller si occupa di gestire tutte le operazioni che avvengono nella scena *new_email_scene* e prevede, oltre i setter, il metodo `handleSendEmail()`. Questo metodo gestisce l'invio della email ottenendo inizialmente i valori inseriti (destinatari, oggetto e corpo). Nel caso in cui l'oggetto non sia stato inserito viene sostituito con "(no subject)". Dopo aver recuperato i valori, il metodo verifica che ogni email inserita sia corretta e, in caso affermativo, viene invocato il metodo `requestSendEmail` del *model*. In base all'esito dell'operazione viene mostrato il messaggio corrispondente.

### Model

`Model` è la classe che contiene tutte le variabili dell'applicazione e si occupa della loro modifica. La classe contiene un oggetto di tipo `Connection` necessario per la comunicazione client/server, l'utente, una lista di email (`ObservableList<Email>`).

Il metodo `connectUser()` fa richiesta di connessione al server tramite socket.

Il metodo `refreshEmailList()` crea un thread che invoca ogni cinque secondi il metodo `retrieveEmails()` necessario per ottenere le nuove email arrivate al server.

Il metodo `requestSendMail()` converte la stringa di destinatari in una lista di utenti e verifica che la lista non sia vuota. In caso affermativo effettua la connessione con il server (`connectUser()`), crea un oggetto di tipo `Email` e invoca il metodo `sendEmail()` di `Connection`. Nel caos in cui l'email sia stata inviata correttamente viene impostata come letta e viene aggiunta in cima alla lista di email.

Il metodo `requestDeleteEmail()` richiede a `Connection` di cancellare un'email dato il suo id. Questo metodo richiede l'id e non direttamente l'email selezionata che si vuole rimuovere è di tipo `EmailProperty` non `Email`. Per questo motivo viene richiesto l'id e subito dopo viene cercata l'email con quell'uuid (`getEmailById()`). Se esiste un email con quell'id allora viene invocato il metodo `deleteEmail()` di `Connection` e se l'operazione va a buon fine l'email viene rimossa dalla lista di email memorizzate nel *model*.

Il metodo `retrieveEmails()` è sincronizzato, in modo tale che se più thread tentano di eseguire questo metodo, non lo fanno in contemporanea. Se il metodo è disponibile, il primo thread può accedere, mentre se è occupato il thread viene messo in coda fino a quando non è disponibile.
Il metodo effettua la connesione con il server, richiede le email al server e le filtra in modo da aggiungere alla lista presente nel *model* sono quelle nuove.

```java
synchronized public void retrieveEmails() {
  try {
    connectUser();
    List<Email> newEmail = this.connection.getEmails();
    // TODO da consegna dovrebbe creare un popup nel caso di nuove mail, in pratica le aggiunge semplicemente in testa

    if (this.emailsSent.isEmpty()) {
      List<Email> send = newEmail.stream().filter(e -> e.getSender().equals(this.user)).collect(Collectors.toList());
      this.emailsSent.addAll(send);
    }

    synchronized (this.emails) {
      if (!newEmail.isEmpty()) {
        List<Email> received = newEmail.stream().filter (e -> e.recipientsAsString().contains(this.user.getUserName())).distinct().collect(Collectors.toList());

        updateLists(received, this.emailReceived);
        updateLists(newEmail, this.emails);
      }
    }
  } catch (SocketException e) {
    e.printStackTrace();
  }
}

private void updateLists (List<Email> newEmails, ObservableList<Email> emailList) {
  newEmails.removeAll(emailList);
  emailList.addAll(0, newEmails);
}  
```

Il metodo `setEmailReadorNot()` permette di richiedere al server di modificare il valore di lettura della specifica mail. Inanzitutto viene effettuata la connessione client-server, poi viene invocato il metodo `setRead()` di `Connection`. Nel caso in cui l'operazione sia andata a buon fine, l'email viene settata come letta/non letta anche lato client (vengono modificate direttamente le mail lato client). In base al tipo di email (inviata o ricevuta) viene settata come letta anche l'email contenuta nella specifica lista (lista di email inviate, lista di email ricevute).

Il metodo `getEmailByUUID()` si occupa di ottenere l'email corrispondente ad un dato uuid. Nel caso in cui la lista di email non sia nulla, viene filtrata mantenendo solo le mail il cui id corrisponde a quello cercato. Se la lista di email filtrate non è nulla oppure vuota, viene ritornata la prima email della lista. <br>
Un'altra versione del metodo permette di fare la ricerca di una email dato l'id in una specifica lista data in input.

Il metodo `closeConnection()` invoca il metodo `close()` di connection per la chiusura della connessione client-server.

### Connection

La classe `Connection` si occupa direttamente della connessione del client con il server e prevede le seguenti variabili:
- `socket` per la gestione della connessione (tipo `Socket`);
- `user`, l'utente client (tipo `User`);
- `inputStream`, stream per ricevere dati dal server (tipo `ObjectInputStream`);
- `outputStream`, stream per inviare dati al server (tipo `ObjectOutputStream`);
- isConnected, valore che indica se il client è connesso con il server (tipo `boolean`).

Il metodo `connect()` si occupa della creazione della connessione con il server. Inizialmente viene inizializzato il socket e, nel caso non sia nullo, vengono inizializzati i due stream di input e output.

```java
this.socket = new Socket(InetAddress.getLocalHost().getHostName(), 8189);
if (socket != null) {
  this.outputStream = new ObjectOutputStream(socket.getOutputStream());
  this.inputStream = new ObjectInputStream(socket.getInputStream());
  this.isConnected = true;
```

Il metodo `close()` permette di chiudere la connessione con il server. Per farlo, nel caso in cui il socket non sia nullo, invia al server il codice (stringa) "close_connection". In questo modo il server sa che deve chiudere la connessione. In questo modo è possibile invocare il metodo `close()` sia sugli stream che sul socket. 

Il metodo `login()` invia al server l'oggeto `User` e ottiene come risposta un valore booleano che dipende se l'email dell'utente esiste o meno. Nel caso in cui l'output stream non sia nullo, viene creato ed inviato al server un oggetto tipo `Command` con l'utente `user`, il comando "login" e la lista di email nulla. Il server ritorna un valore che viene letto nell'input stream tramite metodo `readObject()`. Nel caso in cui  l'oggetto ritornato sia booleano, questo viene ritornato dalla funzione, `false` altrimenti.

Il metodo `getEmails()` richiede al server tutte le email dell'utente. Nel caso in cui l'`outputStream` non sia nullo, viene creato ed inviato al server un oggetto di tipo `Command` con l'utente `user`, il comando "read_emails" e la lista di email nulla. Successivamente ci si mette in attesa della risposta del server attraverso il metodo `inputStream.readObject()`. Nel caso in cui l'oggetto ritornato dal server è di tipo `EmailBox` allora vengono recueprate tutte le email contenute nella casella.

Il metodo `sendEmail()` permette l'invio di una mail dal client dell'utente al server. Nel caso in cui lo stream di output non sia nullo, viene creato ed inviato tramite stream un oggetto di tipo `Command` con l'utente `user`, il codice "send_email" e l'email `email` da inviare. Si attende la risposta del server con il metodo `readObject()` e, nel caso in cui l'oggetto ritornato sia booleano viene generato un messaggio di avvenuto invio (se `true`) o di invio fallito (se `false`).

Il metodo `deleteEmail()` permette di fare richiesta al server di eliminare un email. Nel caso in cui l'`outputStream` non sia nullo, viene creato ed inviato un oggetto di tipo `Command` al server con l'utente `user`, il comando/codice "delete_email" e l'email da eliminare. Il server invierà al client (tramite `inputStream`) un valore booleano che indica il successo o il fallimento dell'operazione.

Il metodo `setRead()` permette di richiedere al server di impostare un'email come letta/non letta. Il metodo richiede la mail e il valore booleano. Nel caso il cui lo stream di output non sia nullo, viene creato un oggetto di tipo `Command` con i valori dell'utente, il codice "set_email_read" e la mail interessata. Il comando viene inviato al server tramite stream di output (`outputStream`) e viene letta la risposta tramite il metodo `readObject()` sullo stream di input. Nel caso in cui l'oggetto inviato dal server sia un valore booleano, questo indica il successo o il fallimento dell'operazione. 

## Package server 

Nel package *server* sono presenti tutte le classi e i metodi necessari alla gestione e creazione del server. In particolare il server si occupa di gestire e memorizzare le email inviate dai singoli client/utenti, TODO.

### Main

La classe `Main` estende `Applicatio` e fa l'override dei metodi `start()` e `close()`. Il metodo `start()` carica la scena *server_scene*, apre la finestra, ottiene il controller, inizializza un oggetto di tipo `PrintStream` per il log sulla finestra e crea un oggetto di tipo `Server`. Il metodo `stop()` invoca il metodo `stopServer()` della classe `Server`.

### Controller

La classe `Controller` si occupa della gestione delle componenti della *view* e dell'interazione con il `Model`. La classe prevede le seguenti variabili:

- `ps`, necessaria per la scrittura dei log sulla finestra del server (tipo `PrintStream`);
- `server`, necessaria per l'apertura, chiusura e gestione del `serverSocket` (tipo `Server`);
- `console` e `toggle_server_status`, componenti dell'interfaccia grafica utili rispettivamente a scrivere del testo ed a aprire/chiudere il server (tipo `TextArea` e `ToggleButton`).

Il metodo `initialize()` inizializza l'oggetto `PrintStream` utilizzando l'oggetto `TextArea` della *view`.

Il metodo `handleServerStatus()` viene invocato quando viene premuto il pulsante. In base al valore del toggle (*true*/*false*) viene invocato il metodo `start()` o `pause()` per l'apertura/chiusura del server con i metodi `startServer()` e `stopServer()`.

### Server

La classe `Server` estende `Thread` e prevede i metodi `run()`, `startServer()` e `stopServer()`.
Il metodo `run()`  crea un thread pool, poi un `serverSocket` utile a gestire la connessione tra il server e il client. L'accettazione del *task* da eseguire avviene tramite il metodo `serverSocket.accept()` ed eseguita con `executorService.execute(connection)`. <br>
Il metodo `startServer()` crea un *thread* e lo fa partire.
Il metodo `stopServer()` TODO

### Connection

La classe `Connection` gestisce la connessione e la gestione della richiesta del client. La classe implementa l'interfaccia `Runnable` e fa quindi l'override del metodo `run()`. `Connection` ha le seguenti variabili:

- `ps`, necessario per stampare sulla finestra i risultati delle operazioni (tipo `PrintStream`);
- `model`, in cui sono contenute tutte le informazioni e i metodi necessari alla loro modifica;
- `socket`, necessario per la comunicazione (scambio di dati) client-server;
- `user`;
- `inputStream` e `outputStream`, canali di comunicazione per lo scambio dei dati (tipo `ObjectInputStream` e `ObjectOutputStream`);
- `closed`, TODO.

Quando viene inizializzato un oggetto di tipo `Connection` vengono, tra le altre cose, definiti l'`inputStream` e l'`outputStream` tramite il *socket*.

Il metodo `run()`, nel caso in cui la connessione non sia chiusa, si mette in attesa e legge l'input proveniente dal client (`inputStream.readObject()`). Nel caso in cui l'oggetto inviato dal client sia di tipo `Command` viene invocato il metodo `handlCall()` per gestire la richiesta ed eseguire l'operazione.
Infine, nel caso in cui entrambi gli stream non siano nulli e nel caso in cui la connessione non sia già chiusa (`closed != null`) viene invocato il metodo `closeConnection()` per chiudere la connessione server-client.

Il metodo `handleCall()` gestisce tutte le possibili richieste fatte dal client. Nel caso in cui la connessione non sia chiusa, uno *switch* basato sul `commandKey` smista le varie operazioni da compiere. Le operazioni che il server può gestire sono:

| commandKey | Operation   |
|------------|-------------|
| login      | `loginUser(command.getUser())` |
| close_connection | `freeUser()` |
| read_emails | `readEmails()` |
| send_email | `sendEmail(command.getEmails());` |
| set_email_read | `setEmailRead(command.getEmails(), command.getUser(), true);` |
| set_email_unread | `setEmailRead(command.getEmails(), command.getUser(), false);` |
| delete_email | `deleteEmail(command.getEmails(), command.getUser())` |

Una volta completata l'operazione viene invocato il metodo `closeConnection()` per chiudere la connessione server-client.

Il metodo `closeConnection()` si occupa di chiudere la connessione nel caso in cui non sia stata già chiusa. In particolare chiude entrambi gli stream (`inputStream.close()`) e il socket, setta a `null` il *model* e l'utente.

Il metodo `loginUser()` si occupa di verifica se l'utente che ha fatto richiesta di login esiste nel server. Nel caso in cui la connessione non sia chiusa, si invoca il metodo `verifyUser()` e, nel caso in cui l'utente risulti essere "registrato" viene aggiunto l'utente nel *model, viene settato l'utente nella connessione corrente, viene creata o recuperata la sua casella email (`getOrCreateEmailBox()`) e viene inviato il valore `true` al client. In caso contrario (l'utente non è presente nella lista di utenti registrati) viene ritornato al client il valore `false` e la connessione viene chiusa.

Il metodo `verifyUser()` invoca il metodo `model.existUser()` per verifica se l'utente è presente tra quelli "registrati".

Il metodo `readEmails()` permette, nel caso in cui la connessione non sia chiusa, di ottenere la casella email dell'utente che ha fatto la richiesta tramite il metodo `model.getEmailBox(). La casella email viene poi inviata tramite stream output al client.

Il metodo `sendEmail()` permette di eseguire l'operazione di invio di una email. Nel caso in cui la connessione non sia chiusa, viene invocato il metodo `model.sendEmail()` e viene inviato al client il valore del risultato dell'operazione (tipo `boolean`).

Il metodo `setEmailRead()` permette di modificare il valore di lettura di una specifica mail. Nel caso in cui la connessione non sia chiusa, viene invocato il metodo `model.setEmailRead()` nel quale viene specifica l'email e il valore di lettura da modificare (*true* o *false*). Infine viene inviato al client il risultato dell'operazione e viene mostrato nella finestra del server il log dell'esecuzione.

Il metodo `deleteEmail()` gestisce l'eliminazione di una specifica email in una specifca casella (*email box*). Nel caso in cui la connessione non sia chiusa, viene invocato il metodo `model.deleteEmail()` e il risultato viene inviato al client.

### Model

La classe `Model` contiene tutte le variabili necessarie al server e tutti i metodi che permettono la loro modifica. Le variabili sono:

- `dataPath`, percorso nel quale sono presenti tutti i file necessari al funzionamento del server e dell'applicazione;
- `ps`, TODO;
- `connectedUser`, lista di utenti attualmente connessi con il server;
- `userList`, lista di tutti gli utenti registrati al sistema.

Il metodo `getOrCreateEmailBox()` permette di ottenere la casella email dell'utente specificato. Nel caso in cui l'email box non esista ne viene creata una vuota. Inizialmente viene invocato il metodo `getEmailBox()` e, nel caso in cui l'`EmailBox` ritornata sia `null`, viene eseguito

```java
createEmptyEmailBox(user);
emailBox = getEmailBox(user);
```

Il metodo `getEmailBox()` ricerca nel percorso `dataPath` il file json corrispondente all'utente cercato. I file json, i quali corrispondono alla casella email dell'utente, contengono tutte le sue email (inviate e ricevute). Il file ha la struttura *nome_utente.json*. Viene creato un oggetto `File` in cui il path è `this.dataPath + user.getUserName() + ".json"`. Nel caso in cui il esiste e sia un file, viene creato un `BufferedReader` per la sua lettura. Dopo aver letto il file json è necessario convertirlo in un oggetto `EmailBox` in modo tale da poter utilizzare il suo contenuto. La conversione viene fatta grazie alla libreria *Gson* nel seguente modo

```java
jsonFile = new BufferedReader(new FileReader(filePath));
Gson gson = new Gson();
emailBox = gson.fromJson(jsonFile, EmailBox.class);
```

Infine la funzione ritorna l'email box.

Il metodo `sendEmail()` si occupa di memorizzare l'email inviata nella casella email del mittente e dei vari destinatari. Per ogni utente indicato nella lista di destinatari dell'email viene verificato se l'utente risulta essere registrato (metodo `existsUser()`). In caso affermativo viene recuperata la casella email con il metodo `getOrCreateEmailBox()` e viene aggiunta l'email alla casella con il metodo `addEmailToEmailBox()`. Nel caso in cui l'utente non risulti essere registrato, viene creata un'email di errore (metodo `createErrorEmail()`) e viene inviata (aggiunta alla casella email) del mittente. In questo modo il mittente, nel momento in cui richiede l'aggiornamento delle email, viene notificato del fatto che quella mail non è stata inviata a quello specifico utente in quanto non esistente.
Infine, nel caso in cui l'operazione di invio delle email sia andato a buon fine e nel caso in cui il mittente non compaia nella lista di destinatari, l'email viene settata come letta (in quanto inviata) e aggiunta nell'`emailBox` del mittente.

Il metodo `deleteEmail()` si occupa di cercare una specifica email in una casella ed eliminarla. Inizialmente viene recuperata la casella email dell'utente (`getEmailBox()`) e, nel caso in cui non sia nulla, viene rimossa l'email interessata dalla lista di email dell'email box. Infine viene sovrascritta l'email box con il metodo `writeEmailBoxAsJSON()`.

Il metodo `createErrorEmail()` ha l'obiettivo di segnalare (tramite una email del sistema) il mittente di una mail che l'email inviata non è stata inviata ad uno dei destinatari in quanto non esistente. In particolare vengono impostati i seguenti valori:

| Campo | Operazione   |
|------------|-------------|
| Utente      | `new User ("Mail Delivery System")` |
| Oggetto | `Undelivered Mail Returned to Sender` |
| Corpo | `"This is a system-generated message to inform you that your email could not be delivered to following recipients. Details of the email and the error are as follows: ...` |

Infine viene ritornata un oggetto di tipo `Email` con i valori appena descritti.

Il metodo `setEmailRead()` si occupa di modificare il valore di lettura di una specifica email di una specifica casella. Inizialmente viene recueprata la casella email dell'utente e se non è nulla viene effettuata la ricerca della mail cercata tra la lista di email nella casella. Infine viene sovrascritta la casella email con il metodo `writeEmailBoxAsJSON()`.

Il metodo `addEmailToEmailBox()` permette di aggiungere una email all'email box di un utente e memorizzare il tutto nel corrispondente file json. Per fare questo la mail viene aggiunta all'`emailBox` e vengono inizializzati degli oggetti `Writer` e `Gson` utili alla scrittura della classe su json.

```java
Writer writer = new FileWriter(filePath);
Gson gson = new GsonBuilder().setPrettyPrinting().create();
gson.toJson(emailBox, writer);
writer.close();
```

Il metodo `createEmptyEmailBox()` permette di creare una casella email vuota. Nel caso in cui l'utente non sia nullo, viene creato un oggetto di tipo `EmailBox` con l'utente e una lista vuota di `Email`. Infine viene invocato il metodo `writeEmailBoxAsJSON()` per la scrittura dell'email box su file json.

Il metodo `writeEmailBoxAsJSON()` permette di salvare su file un `EmailBox`. Nel caso in cui l'utente non sia nullo, vengono inizializzati degli oggetti `Writer` e `Gson` utili alla scrittura della classe su json.

```java
Writer writer = new FileWriter(filePath);
Gson gson = new GsonBuilder().setPrettyPrinting().create();
gson.toJson(emailBox, writer);
writer.close();
```

Il metodo `initUserList()` permette di recuperare dal file *users.txt* la lista di utenti registrati. Questo viene fatto inizializzando un oggetto `File` e leggendolo riga per riga attraverso un oggetto `Scanner`

```java
Scanner scanner = new Scanner(file);
while (scanner.hasNextLine()) {
  String email = scanner.nextLine();
  this.userList.add(email);
}
scanner.close();
```
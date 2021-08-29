package server;

import lib.LabelMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread{
  private ServerSocket serverSocket;
  private ExecutorService executorService;
  final private PrintStream ps;
  final private List<Connection> connectionList = new ArrayList<>();
  final private List<String> userList = new ArrayList<>(); // TODO Capire se usare
  final private Model model;
  final private int nThread = 5;
  private boolean stop = false;

  Server(PrintStream ps) {
    super();
    this.model = new Model(ps);
    this.ps = ps;
    this.setDaemon(true);
    start();
  }

  /**
   * Creo un thread pool, poi un serverSocket per gestire la connessione tra il server e il client. L'accettazione del "task"
   * avviene tramite serverSocket.accept() ed eseguita con executorService.execute(connection).
   *
   */
  public void run() {
    stop = false;
    this.serverSocket = null;
    executorService = Executors.newFixedThreadPool(nThread);

    try {
      serverSocket = new ServerSocket(8189);
      ps.println(LabelMessage.serverOpen);
      while (!stop) {
        try {
          Socket client = serverSocket.accept();
          Connection connection = new Connection(model, client, ps);
          executorService.execute(connection);
          connectionList.add(connection);
          ps.println(LabelMessage.startedNewProcess);
        } catch(SocketTimeoutException ex){
          ps.println(LabelMessage.socketTimout);
        }
      }
    } catch (IOException e){
      if (e.getMessage().contains("Socket closed"))
        ps.println(LabelMessage.suspendedRequestAcceptance);
      else {
        e.printStackTrace();
        stopServer();
      }
    }
  }

  void startServer() {
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  void stopServer() {
    stop = true;

    try {
      this.serverSocket.close();
      ps.println(LabelMessage.serverClosingSocket);
    } catch (IOException e) {
      ps.println(LabelMessage.serverClosingSocketError + " " + e.getMessage());
    }

    executorService.shutdownNow();
    if (!executorService.isTerminated()) {
      for (Connection c : this.connectionList) {
        if (c != null && !c.isClosed()) {
          c.closeConnection();
        }
      }
    }

    if (executorService.isTerminated()) {
      ps.println(LabelMessage.serverTermined);
    } else {
      ps.println(LabelMessage.serverNotTermined);
      ps.println(LabelMessage.retryClosingServer);
    }

    retryClosingServer();
  }

  private void retryClosingServer() {
    if (this.serverSocket.isClosed() && executorService.isTerminated()) {
      ps.println(LabelMessage.serverClosed);
    } else {
      ps.println(LabelMessage.serverOpen);
      try {
        sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (executorService.isTerminated()) {
        ps.println(LabelMessage.serverClosed);
      } else {
        stopServer();
      }
    }
  }
}

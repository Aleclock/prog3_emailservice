package server;

import lib.User;

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
  final private List<Connection> connectionList = new ArrayList();
  final private List<String> userList = new ArrayList<>();
  final private Model model;
  final private int nThread = 5;
  final private boolean stop = false;

  Server(PrintStream ps) {
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
    executorService = Executors.newFixedThreadPool(nThread);
    try {
      serverSocket = new ServerSocket(8189);
      ps.println("Server: avviato.");
      while (!stop) {
        try {
          Socket client = serverSocket.accept();
          Connection connection = new Connection(model, client, ps);
          executorService.execute(connection);
          //connectionList.add(connection);
        } catch(SocketTimeoutException ex){
          //out.println(Controller.srvErr("Server: socket timeout"));
        }
      }
    } catch (IOException e){
      e.printStackTrace();
    }
  }

  void startServer() {
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  void stopServer() {
    // TODO
  }
}

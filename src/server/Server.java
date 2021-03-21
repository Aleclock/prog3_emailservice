package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread{
  private ServerSocket serverSocket;
  private ExecutorService executorService;
  private List<Connection> connectionList = new ArrayList<Connection>();
  private Model model;
  private int nThread;
  private boolean stop = false;

  Server() {
    this.model = new Model();
    this.setDaemon(true);
    start();
  }

  public void run() {
    executorService = Executors.newFixedThreadPool(nThread);
    try {
      serverSocket = new ServerSocket(8189);
      while (!stop) {
        Socket client = serverSocket.accept();
        Connection connection = new Connection(model, client);
        executorService.execute(connection);
        connectionList.add(connection);
      }
    } catch (IOException e){
      e.printStackTrace();}
  }

  void startServer() {
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  void stopServer() {
  }
}

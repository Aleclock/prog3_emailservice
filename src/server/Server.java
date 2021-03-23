package server;

import lib.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread{
  private ServerSocket serverSocket;
  private ExecutorService executorService;
  private List<Connection> connectionList = new ArrayList();
  private List<String> userList = new ArrayList<>();
  private Model model;
  private int nThread = 5;
  private boolean stop = false;

  Server() {
    this.model = new Model();
    initUserList();
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
      while (!stop) {
        try {
          Socket client = serverSocket.accept();
          Connection connection = new Connection(model, client, userList);
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

  private void initUserList(){
    File file = new File("/Users/aleclock/Desktop/uni/prog3/prog3_emailservice/src/server/data/users.txt");
    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String email = scanner.nextLine();
        System.out.println(email);
        userList.add(email);
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  void stopServer() {
  }
}

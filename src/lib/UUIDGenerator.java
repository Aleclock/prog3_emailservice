package lib;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

public class UUIDGenerator {
  final private String folderPath;
  final private String fileName = "uuid.txt";
  private AtomicLong currentUUID;

  public UUIDGenerator(String folderPath) {
    this.folderPath = folderPath;
    this.currentUUID = new AtomicLong();
    initUUIDFile();
  }

  private void initUUIDFile() {
    File file = new File (folderPath + fileName);
    try {
      Scanner scanner = new Scanner(file);
      while(scanner.hasNextLine()) {
        Long fileUUID = Long.parseLong(scanner.nextLine());
        this.currentUUID.set(fileUUID);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  synchronized long getCurrentUUID() {
    return this.currentUUID.get();
  }

  synchronized public long generateUUID() {
    long newUUID = this.currentUUID.incrementAndGet();
    updateFile(newUUID);
    return newUUID;
  }

  synchronized boolean updateFile(Long uuid) {
    boolean result = false;
    try {
      Writer writer = new FileWriter(folderPath + fileName);
      writer.write(String.valueOf(uuid));
      result = true;
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
}

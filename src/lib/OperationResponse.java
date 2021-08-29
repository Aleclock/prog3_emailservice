package lib;

public class OperationResponse<K, V> {
  private K first;
  private V second;

  public OperationResponse(K f, V s) {
    this.first = f;
    this.second = s;
  }

  public K getFirst() {
    return this.first;
  }

  public V getSecond() {
    return this.second;
  }

  public void set(K f,V s) {
    this.first = f;
    this.second = s;
  }

  public void setFirst(K f) {
    this.first = f;
  }

  public void setSecond(V s) {
    this.second = s;
  }
}

package services;


import data.RefactoringEntry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RefactoringsMap {

  public Map<String, RefactoringEntry> map = new ConcurrentHashMap<>();
  public String version;

  public RefactoringsMap() {
  }
  public RefactoringsMap(Map<String, RefactoringEntry> map, String version) {
    this.map = map;
    this.version = version;
  }
}
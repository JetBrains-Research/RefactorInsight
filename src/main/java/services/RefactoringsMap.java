package services;

import data.RefactoringEntry;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Refactorings Map.
 * Contains a version and the refactoring data map.
 */
public class RefactoringsMap {

  public Map<String, RefactoringEntry> map = new ConcurrentHashMap<>();
  public String version;

  public RefactoringsMap() {
  }

  public RefactoringsMap(Map<String, RefactoringEntry> map, String version) {
    this.map = map;
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefactoringsMap that = (RefactoringsMap) o;
    return Objects.equals(map, that.map)
        && Objects.equals(version, that.version);
  }
}
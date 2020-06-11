package services;

import com.intellij.util.xmlb.Converter;
import data.RefactoringEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

class RefactoringsMapConverter extends Converter<RefactoringsMap> {
  public RefactoringsMap fromString(String value) {
    String[] bla = value.split(",", 2);
    return new RefactoringsMap(convertWithStream(bla[1]), bla[0]);
  }

  public String toString(RefactoringsMap value) {
    return value.version + "," + convertWithStream(value.map);
  }

  public Map<String, RefactoringEntry> convertWithStream(String mapAsString) {
    return Arrays.stream(mapAsString.split(","))
        .map(entry -> entry.split("="))
        .collect(Collectors.toMap(entry -> entry[0],
            entry -> RefactoringEntry.fromString(entry[1])));
  }

  public String convertWithStream(Map<String, RefactoringEntry> map) {
    return map.keySet().stream()
        .map(key -> key + "=" + map.get(key).toString())
        .collect(Collectors.joining(","));
  }
}
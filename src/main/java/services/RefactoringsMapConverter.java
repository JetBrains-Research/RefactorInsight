package services;

import com.intellij.util.xmlb.Converter;
import data.RefactoringEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import utils.StringUtils;

class RefactoringsMapConverter extends Converter<RefactoringsMap> {

  public RefactoringsMap fromString(String value) {
    String[] bla = value.split(StringUtils.MAP_DELIMITER, 2);
    return new RefactoringsMap(convertWithStream(bla[1]), bla[0]);
  }

  public String toString(RefactoringsMap value) {
    return value.version + StringUtils.MAP_DELIMITER + convertWithStream(value.map);
  }

  public Map<String, RefactoringEntry> convertWithStream(String mapAsString) {
    return Arrays.stream(mapAsString.split(StringUtils.MAP_DELIMITER))
        .map(entry -> entry.split(StringUtils.MAP_ENTRY_DELIMITER))
        .collect(Collectors.toMap(entry -> entry[0],
            entry -> RefactoringEntry.fromString(entry[1])));
  }

  public String convertWithStream(Map<String, RefactoringEntry> map) {
    return map.keySet().stream()
        .map(key -> key + StringUtils.MAP_ENTRY_DELIMITER + map.get(key).toString())
        .collect(Collectors.joining(StringUtils.MAP_DELIMITER));
  }
}
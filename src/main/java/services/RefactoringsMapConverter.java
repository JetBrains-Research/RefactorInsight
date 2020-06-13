package services;

import com.intellij.util.xmlb.Converter;
import data.RefactoringEntry;
import java.util.Arrays;
import java.util.stream.Collectors;
import utils.StringUtils;

class RefactoringsMapConverter extends Converter<RefactoringsMap> {

  public RefactoringsMap fromString(String value) {
    String[] bla = value.split(StringUtils.MAP_DELIMITER, 2);
    return new RefactoringsMap(Arrays.stream(bla[1].split(StringUtils.MAP_DELIMITER))
        .map(entry -> entry.split(StringUtils.MAP_ENTRY_DELIMITER))
        .collect(Collectors.toMap(entry -> entry[0],
            entry -> RefactoringEntry.fromString(entry[1]))), bla[0]);
  }

  public String toString(RefactoringsMap value) {
    return value.version + StringUtils.MAP_DELIMITER
        + value.map.keySet().stream()
        .map(key -> key + StringUtils.MAP_ENTRY_DELIMITER + value.map.get(key).toString())
        .collect(Collectors.joining(StringUtils.MAP_DELIMITER));
  }

}
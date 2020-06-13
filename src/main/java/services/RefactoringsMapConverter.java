package services;

import static utils.StringUtils.MAP;
import static utils.StringUtils.MAP_ENTRY;
import static utils.StringUtils.delimiter;

import com.intellij.util.xmlb.Converter;
import data.RefactoringEntry;
import java.util.Arrays;
import java.util.stream.Collectors;

class RefactoringsMapConverter extends Converter<RefactoringsMap> {

  public RefactoringsMap fromString(String value) {
    String regex1 = delimiter(MAP, true);
    String regex2 = delimiter(MAP_ENTRY, true);
    String[] tokens = value.split(regex1, 2);
    return new RefactoringsMap(Arrays.stream(tokens[1].split(regex1))
        .map(entry -> entry.split(regex2))
        .collect(Collectors.toMap(entry -> entry[0],
            entry -> RefactoringEntry.fromString(entry[1], entry[0]))), tokens[0]);
  }

  public String toString(RefactoringsMap value) {
    return value.version + delimiter(MAP)
        + value.map.keySet().stream()
        .map(key -> key + delimiter(MAP_ENTRY) + value.map.get(key).toString())
        .collect(Collectors.joining(delimiter(MAP)));
  }

}
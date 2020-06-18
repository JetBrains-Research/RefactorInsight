package services;

import static utils.StringUtils.MAP;
import static utils.StringUtils.MAP_ENTRY;
import static utils.StringUtils.delimiter;

import com.intellij.util.xmlb.Converter;
import data.RefactoringEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * RefactoringsMap converter that serializes and deserializes data.
 * Converts the data in the .xml to a RefactoringMap object.
 * Converts a RefactoringMap object into a string that can be stored in refactorings.xml.
 */
public class RefactoringsMapConverter extends Converter<RefactoringsMap> {

  /**
   * Deserializes the data in the .idea/refactorings.xml into a refactorings
   * map object.
   * @param value to be converted
   * @return a refactorings map
   */
  public RefactoringsMap fromString(String value) {
    try {
      String regex1 = delimiter(MAP, true);
      String regex2 = delimiter(MAP_ENTRY, true);
      String[] tokens = value.split(regex1, 2);
      return new RefactoringsMap(Arrays.stream(tokens[1].split(regex1))
          .map(entry -> entry.split(regex2))
          .collect(Collectors.toMap(entry -> entry[0],
              entry -> RefactoringEntry.fromString(entry[1], entry[0]))), tokens[0]);
    } catch (Exception e) {
      System.out.println("Deprecated xml format");
      return new RefactoringsMap(new HashMap<>(), "-1");
    }
  }

  /**
   * Converts a RefactoringMap object into a string that can be stored
   * in the .idea/refactorings.xml file.
   * @param value map to be converted
   * @return a string representation of the refactorings map
   */
  public String toString(RefactoringsMap value) {
    return value.version + delimiter(MAP)
        + value.map.keySet().stream()
        .map(key -> key + delimiter(MAP_ENTRY) + value.map.get(key).toString())
        .collect(Collectors.joining(delimiter(MAP)));
  }

}
package services;

import static junit.framework.TestCase.assertEquals;
import static utils.StringUtils.ENTRY;
import static utils.StringUtils.MAP;
import static utils.StringUtils.MAP_ENTRY;
import static utils.StringUtils.delimiter;

import data.RefactoringEntry;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ConverterTest {


//  String invalid = "1.0.5" + delimiter(MAP) + "";
//  String oneEntry = invalid + "abcd" + delimiter(MAP_ENTRY) + "cdef"
//      + delimiter(ENTRY) + 1234 + delimiter(ENTRY);
//
//  Map<String, RefactoringsMap> expected = Map.of(
//      invalid, new RefactoringsMap(new HashMap<>(), "-1"),
//      oneEntry, new RefactoringsMap(Map.of("abcd",
//          new RefactoringEntry("abcd", "cdef", 1234)), "1.0.5")
//  );

  @Test
  public void mapConverterTest(){
//    RefactoringsMapConverter converter = new RefactoringsMapConverter();
//    expected.forEach((k, v) -> assertEquals(converter.fromString(k), v));
  }
}

package services;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static utils.StringUtils.ENTRY;
import static utils.StringUtils.FRAG;
import static utils.StringUtils.INFO;
import static utils.StringUtils.MAP;
import static utils.StringUtils.MAP_ENTRY;
import static utils.StringUtils.RANGE;
import static utils.StringUtils.delimiter;

import com.intellij.diff.fragments.LineFragmentImpl;
import data.Group;
import data.RefactoringEntry;
import data.RefactoringInfo;
import data.diff.MoreSidedDiffRequestGenerator;
import data.diff.ThreeSidedDiffRequestGenerator;
import data.diff.TwoSidedDiffRequestGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Test;

public class ConverterTest {

  @Test
  public void mapConverterTest() {
    //Test case 1
    String invalidString = "-1" + delimiter(MAP) + "";
    RefactoringsMap invalidMap = new RefactoringsMap(new HashMap<>(), "-1");
    //Test case 2
    String oneEntryString = "1.0.5" + delimiter(MAP) + "cccc"
        + delimiter(MAP_ENTRY) + "bbbb"
        + delimiter(ENTRY) + 1234 + delimiter(ENTRY);
    RefactoringsMap oneEntryMap = new RefactoringsMap(Map.of("cccc",
        new RefactoringEntry("cccc", "bbbb", 1234)
            .setRefactorings(new ArrayList<>())), "1.0.5");
    //Test case 3
    String moreEntryString = oneEntryString
        + delimiter(MAP) + "bbbb" + delimiter(MAP_ENTRY) + "aaaa"
        + delimiter(ENTRY) + 5678 + delimiter(ENTRY);
    RefactoringsMap moreEntryMap = new RefactoringsMap(Map.of(
        "cccc", new RefactoringEntry("cccc", "bbbb", 1234)
            .setRefactorings(new ArrayList<>()),
        "bbbb", new RefactoringEntry("bbbb", "aaaa", 5678)
            .setRefactorings(new ArrayList<>())
    ), "1.0.5");
    RefactoringsMapConverter converter = new RefactoringsMapConverter();
    Map.of(
        invalidString, invalidMap,
        oneEntryString, oneEntryMap,
        moreEntryString, moreEntryMap
    ).forEach((k, v) -> {
      assertEquals(converter.fromString(k), v);
      String[] expected = k.split(delimiter(MAP));
      String[] actual = converter.toString(v).split(delimiter(MAP));
      Arrays.sort(expected);
      Arrays.sort(actual);
      assertTrue(Arrays.deepEquals(actual, expected));
    });
  }

  @Test
  public void infoConverterTest() {
    //Test case 1
    String noMarkingsString = String.join(delimiter(INFO), "name",
        "nameBef", "nameAft", "elemBef", "elemAft", "detBef", "detAft",
        "left/path.java", "mid/path.java", "right/path.java",
        "CLASS", "t", "f", "f", "", "");
    RefactoringInfo noMarkingsInfo = new RefactoringInfo()
        .setName("name")
        .setNameBefore("nameBef")
        .setNameAfter("nameAft")
        .setElementBefore("elemBef")
        .setElementAfter("elemAft")
        .setDetailsBefore("detBef")
        .setDetailsAfter("detAft")
        .setLeftPath("left/path.java")
        .setMidPath("mid/path.java")
        .setRightPath("right/path.java")
        .setGroup(Group.CLASS)
        .setThreeSided(true)
        .setHidden(false)
        .setMoreSided(false)
        .setIncludes(new HashSet<>())
        .setRequestGenerator(new ThreeSidedDiffRequestGenerator());
    //Test case 2
    String oneMarkingString = String.join(delimiter(INFO), "name",
        "nameBef", "nameAft", "elemBef", "elemAft", "detBef", "detAft",
        "left/path.java", "mid/path.java", "right/path.java",
        "CLASS", "f", "f", "f", String.join(delimiter(FRAG),
            "1", "2", "3", "4", "0", "0", "0", "0", ""), "");
    RefactoringInfo oneMarkingInfo = new RefactoringInfo()
        .setName("name")
        .setNameBefore("nameBef")
        .setNameAfter("nameAft")
        .setElementBefore("elemBef")
        .setElementAfter("elemAft")
        .setDetailsBefore("detBef")
        .setDetailsAfter("detAft")
        .setLeftPath("left/path.java")
        .setMidPath("mid/path.java")
        .setRightPath("right/path.java")
        .setGroup(Group.CLASS)
        .setThreeSided(false)
        .setHidden(false)
        .setMoreSided(false)
        .setIncludes(new HashSet<>())
        .setRequestGenerator(new TwoSidedDiffRequestGenerator(Arrays.asList(
            new LineFragmentImpl(1, 2, 3, 4, 0, 0, 0, 0))));
    //Test case 3
    String moreSidedString = String.join(delimiter(INFO), "name",
        "nameBef", "nameAft", "elemBef", "elemAft", "detBef", "detAft",
        "left/path.java", "mid/path.java", "right/path.java",
        "CLASS", "f", "f", "t", String.join(delimiter(RANGE),
            "1", "2", "3", "4", "0", "0", "0", "0", "extraction/left/path.java"), "");
    RefactoringInfo moreSidedInfo = new RefactoringInfo()
        .setName("name")
        .setNameBefore("nameBef")
        .setNameAfter("nameAft")
        .setElementBefore("elemBef")
        .setElementAfter("elemAft")
        .setDetailsBefore("detBef")
        .setDetailsAfter("detAft")
        .setLeftPath("left/path.java")
        .setMidPath("mid/path.java")
        .setRightPath("right/path.java")
        .setGroup(Group.CLASS)
        .setThreeSided(false)
        .setHidden(false)
        .setMoreSided(true)
        .setIncludes(new HashSet<>())
        .setRequestGenerator(new MoreSidedDiffRequestGenerator(Arrays.asList(
            new MoreSidedDiffRequestGenerator.MoreSidedRange(1, 2, 3, 4,
                0, 0, 0, 0, "extraction/left/path.java"))));

    Map.of(
        noMarkingsString, noMarkingsInfo,
        oneMarkingString, oneMarkingInfo,
        moreSidedString, moreSidedInfo
    ).forEach((k, v) -> {
      assertEquals(RefactoringInfo.fromString(k), v);
      assertEquals(k, v.toString());
    });
  }
}

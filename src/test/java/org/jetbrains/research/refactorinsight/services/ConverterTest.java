package org.jetbrains.research.refactorinsight.services;

import static junit.framework.TestCase.assertEquals;
import static org.jetbrains.research.refactorinsight.utils.StringUtils.*;
import static org.junit.Assert.assertTrue;

import com.intellij.diff.fragments.LineFragmentImpl;
import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.research.refactorinsight.data.diff.MoreSidedDiffRequestGenerator;
import org.jetbrains.research.refactorinsight.data.diff.ThreeSidedDiffRequestGenerator;
import org.jetbrains.research.refactorinsight.data.diff.TwoSidedDiffRequestGenerator;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;
import org.junit.Test;

public class ConverterTest {

  @Test
  public void mapConverterTest() {
    //Test case 1
    String invalidString = "-1" + delimiter(MAP) + "";
    RefactoringsMap invalidMap = new RefactoringsMap(new ConcurrentHashMap<>(), "-1");
    //Test case 2
    String oneEntryString = "1.0.5" + delimiter(MAP) + "cccc"
        + delimiter(MAP_ENTRY) + "bbbb"
        + delimiter(ENTRY) + 1234
        + delimiter(ENTRY) + "true"
        + delimiter(ENTRY);
    final RefactoringEntry refactoringEntry = new RefactoringEntry("cccc", "bbbb", 1234);
    refactoringEntry.setTimeout(true);
    RefactoringsMap oneEntryMap =
        new RefactoringsMap(new ConcurrentHashMap<>(Map.of("cccc",
                                                           refactoringEntry
                                                               .setRefactorings(new ArrayList<>()))), "1.0.5");
    //Test case 3
    String moreEntryString = oneEntryString
        + delimiter(MAP) + "bbbb" + delimiter(MAP_ENTRY) + "aaaa"
        + delimiter(ENTRY) + 5678
        + delimiter(ENTRY) + "false" + delimiter(ENTRY);
    final RefactoringEntry refactoringEntry1 = new RefactoringEntry("bbbb", "aaaa", 5678);
    RefactoringsMap moreEntryMap = new RefactoringsMap(new ConcurrentHashMap<>(Map.of(
        "cccc", refactoringEntry.setRefactorings(new ArrayList<>()),
        "bbbb", refactoringEntry1.setRefactorings(new ArrayList<>()))), "1.0.5");
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
    String noMarkingsString = String.join(delimiter(INFO),
        "45",
        "nameBef", "nameAft", "elemBef", "elemAft", "detBef", "detAft",
        "left/path.java", "mid/path.java", "right/path.java",
        "1",
        "t", "", "", "t",
        "",
        "", "", "",
        "");
    RefactoringInfo noMarkingsInfo = new RefactoringInfo()
        .setType(RefactoringType.ADD_ATTRIBUTE_ANNOTATION)
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
    String oneMarkingString = String.join(delimiter(INFO),
        "54",
        "nameBef", "nameAft", "elemBef", "elemAft", "detBef", "detAft",
        "left/path.java", "mid/path.java", "right/path.java",
        "1",
        "", "", "", "",
        String.join(delimiter(FRAG), "1", "2", "3", "4", "0", "0", "0", "0", ""),
        "", "1/2/3", "",
        "");
    RefactoringInfo oneMarkingInfo = new RefactoringInfo()
        .setType(RefactoringType.ADD_PARAMETER)
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
        .setChanged(false)
        .setIncludes(new HashSet<>())
        .setFoldingDescriptorMid(new FoldingDescriptor(1, 2, 3))
        .setRequestGenerator(new TwoSidedDiffRequestGenerator(Collections.singletonList(
            new LineFragmentImpl(1, 2, 3, 4, 0, 0, 0, 0))));
    //Test case 3
    String moreSidedString = String.join(delimiter(INFO),
        "59",
        "nameBef", "nameAft", "elemBef", "elemAft", "detBef", "detAft",
        "left/path.java", "mid/path.java", "right/path.java",
        "6",
        "", "", "t", "t",
        String.join(delimiter(RANGE), "1", "2", "3", "4", "0", "0", "0", "0", "extraction/left/path.java"),
        "12/13/14", "", "15/16/17",
        "");
    RefactoringInfo moreSidedInfo = new RefactoringInfo()
        .setType(RefactoringType.RENAME_AND_CHANGE_VARIABLE_TYPE)
        .setNameBefore("nameBef")
        .setNameAfter("nameAft")
        .setElementBefore("elemBef")
        .setElementAfter("elemAft")
        .setDetailsBefore("detBef")
        .setDetailsAfter("detAft")
        .setLeftPath("left/path.java")
        .setMidPath("mid/path.java")
        .setRightPath("right/path.java")
        .setGroup(Group.PACKAGE)
        .setThreeSided(false)
        .setHidden(false)
        .setMoreSided(true)
        .setChanged(true)
        .setIncludes(new HashSet<>())
        .setFoldingDescriptorBefore(new FoldingDescriptor(12, 13, 14))
        .setFoldingDescriptorAfter(new FoldingDescriptor(15, 16, 17))
        .setRequestGenerator(new MoreSidedDiffRequestGenerator(Collections.singletonList(
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

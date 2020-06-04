package data;

import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.fragments.MergeLineFragment;
import com.intellij.diff.fragments.MergeLineFragmentImpl;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.util.text.MergeInnerDifferences;
import com.intellij.diff.util.MergeConflictType;
import com.intellij.diff.util.TextDiffType;
import com.intellij.openapi.util.TextRange;
import gr.uom.java.xmi.LocationInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RefactoringLine {

  private int leftStart;
  private int leftEnd;
  private int midStart;
  private int midEnd;
  private int rightStart;
  private int rightEnd;
  private List<RefactoringOffset> offsets = new ArrayList<>();
  private ThreeSidedType type;

  /**
   * Data Holder for three sided code ranges.
   *
   * @param leftStart  int
   * @param leftEnd    int
   * @param midStart   int
   * @param midEnd     int
   * @param rightStart int
   * @param rightEnd   int
   */
  public RefactoringLine(int leftStart, int leftEnd, int midStart, int midEnd, int rightStart,
                         int rightEnd, ThreeSidedType type) {
    this.leftStart = leftStart;
    this.leftEnd = leftEnd;
    this.midStart = midStart;
    this.midEnd = midEnd;
    this.rightStart = rightStart;
    this.rightEnd = rightEnd;
    this.type = type;
  }

  public RefactoringLine() {
  }

  /**
   * Generate Conflicttype from ThreeSidedType.
   * Conflict type is used to set highlighting colors and can disable
   * highlighting in the left or right editor.
   */
  public static MergeConflictType getConflictType(ThreeSidedType type) {
    switch (type) {
      case LEFT:
        return new MergeConflictType(TextDiffType.MODIFIED, true, false);
      case RIGHT:
        return new MergeConflictType(TextDiffType.INSERTED, false, true);
      default:
        return new MergeConflictType(TextDiffType.MODIFIED, true, true);
    }
  }

  /**
   * Offset object for three sided diff window.
   * Contains all offset ranges in one object.
   */
  public static MergeInnerDifferences toMergeInnerDifferences(List<RefactoringOffset> offsets) {
    List<TextRange> left =
        offsets.stream().map(RefactoringOffset::getLeftRange).collect(Collectors.toList());
    List<TextRange> mid =
        offsets.stream().map(RefactoringOffset::getMidRange).collect(Collectors.toList());
    List<TextRange> right =
        offsets.stream().map(RefactoringOffset::getRightRange).collect(Collectors.toList());
    return new MergeInnerDifferences(left, mid, right);
  }

  /**
   * Returns coderange in a LineFragment object.
   * This object allows highlighting in the IDEA diff window.
   *
   * @return LineFragment
   */
  public LineFragment getTwoSidedRange(int maxLineLeft, int maxLineRight) {
    rightEnd = rightEnd < 0 ? maxLineLeft : rightEnd;
    leftEnd = leftEnd < 0 ? maxLineRight : leftEnd;
    if (offsets.isEmpty()) {
      return new LineFragmentImpl(leftStart, leftEnd, rightStart, rightEnd,
          0, 0, 0, 0);
    } else {
      return new LineFragmentImpl(leftStart, leftEnd, rightStart, rightEnd,
          0, 0, 0, 0,
          offsets.stream().map(RefactoringOffset::toDiffFragment).collect(Collectors.toList()));
    }
  }

  /**
   * Three sided range.
   */
  public SimpleThreesideDiffChange getThreeSidedRange(int maxLineLeft, int maxLineMid,
                                                      int maxLineRight,
                                                      SimpleThreesideDiffViewer viewer) {
    rightEnd = rightEnd < 0 ? maxLineLeft : rightEnd;
    midEnd = midEnd < 0 ? maxLineMid : midEnd;
    leftEnd = leftEnd < 0 ? maxLineRight : leftEnd;
    MergeLineFragment line =
        new MergeLineFragmentImpl(leftStart, leftEnd, midStart, midEnd, rightStart, rightEnd);
    if (offsets.isEmpty()) {
      return new SimpleThreesideDiffChange(line, getConflictType(type), null, viewer);
    } else {
      return new SimpleThreesideDiffChange(line, getConflictType(type),
          toMergeInnerDifferences(offsets), viewer);
    }
  }

  public RefactoringLine addOffset(int beforeStart, int beforeEnd, int afterStart, int afterEnd) {
    offsets.add(new RefactoringOffset(beforeStart, beforeEnd, afterStart, afterEnd));
    return this;
  }

  public RefactoringLine addOffset(LocationInfo left, LocationInfo right) {
    return addOffset(left.getStartOffset(), left.getEndOffset(),
        right.getStartOffset(), right.getEndOffset());
  }

  public RefactoringLine addOffset(LocationInfo left, LocationInfo mid, LocationInfo right) {
    return addOffset(left.getStartOffset(), left.getEndOffset(), mid.getStartOffset(),
        mid.getEndOffset(), right.getStartOffset(), right.getEndOffset());
  }

  public RefactoringLine addOffset(int leftStart, int leftEnd,
                                   int midStart, int midEnd,
                                   int rightStart, int rightEnd) {
    offsets.add(new RefactoringOffset(leftStart, leftEnd, midStart, midEnd, rightStart, rightEnd));
    return this;
  }

  public enum ThreeSidedType {
    LEFT,
    RIGHT,
    BOTH
  }

  public int getRightStart() {
    return rightStart;
  }
}

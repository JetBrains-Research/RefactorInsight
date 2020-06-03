package data;

import com.intellij.diff.fragments.DiffFragment;
import com.intellij.diff.fragments.DiffFragmentImpl;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.fragments.MergeLineFragment;
import com.intellij.diff.fragments.MergeLineFragmentImpl;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.util.text.MergeInnerDifferences;
import com.intellij.diff.util.DiffUtil;
import com.intellij.diff.util.MergeConflictType;
import com.intellij.diff.util.TextDiffType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.util.TextRange;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RefactoringLine {

  private static final int LEFT_START = 0;
  private static final int LEFT_END = 1;
  private static final int MID_START = 2;
  private static final int MID_END = 3;
  private static final int RIGHT_START = 4;
  private static final int RIGHT_END = 5;

  private int[] lines = new int[6];
  private int[] columns = new int[6];
  private List<RefactoringOffset> offsets = new ArrayList<>();
  private ThreeSidedType type;
  private boolean hasColumns = false;

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
    lines[LEFT_START] = leftStart;
    lines[LEFT_END] = leftEnd;
    lines[MID_START] = midStart;
    lines[MID_END] = midEnd;
    lines[RIGHT_START] = rightStart;
    lines[RIGHT_END] = rightEnd;
    this.type = type;
  }

  public RefactoringLine(CodeRange left, CodeRange mid, CodeRange right, ThreeSidedType type) {
    lines[LEFT_START] = left.getStartLine();
    lines[LEFT_END] = left.getEndLine();
    lines[RIGHT_START] = right.getStartLine();
    lines[RIGHT_END] = right.getEndLine();

    columns[LEFT_START] = left.getStartColumn();
    columns[LEFT_END] = left.getEndColumn();
    columns[RIGHT_START] = right.getStartColumn();
    columns[RIGHT_END] = right.getEndColumn();

    if (mid != null) {
      lines[MID_START] = mid.getStartLine();
      lines[MID_END] = mid.getEndLine();

      columns[MID_START] = mid.getStartColumn();
      columns[MID_END] = mid.getEndColumn();
    }
    this.hasColumns = true;
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
  private MergeInnerDifferences toMergeInnerDifferences(String leftText, String midText,
                                                        String rightText) {
    List<TextRange> left =
        offsets.stream().map(RefactoringOffset::getLeftRange).collect(Collectors.toList());
    List<TextRange> mid =
        offsets.stream().map(RefactoringOffset::getMidRange).collect(Collectors.toList());
    List<TextRange> right =
        offsets.stream().map(RefactoringOffset::getRightRange).collect(Collectors.toList());

    if (hasColumns) {
      Document leftDocument = new DocumentImpl(leftText);
      Document midDocument = new DocumentImpl(midText);
      Document rightDocument = new DocumentImpl(rightText);

      left.add(new TextRange(
          DiffUtil.getOffset(leftDocument, lines[LEFT_START], columns[LEFT_START]),
          DiffUtil.getOffset(leftDocument, lines[LEFT_END], columns[LEFT_END])
      ));
      mid.add(new TextRange(
          DiffUtil.getOffset(midDocument, lines[MID_START], columns[MID_START]),
          DiffUtil.getOffset(midDocument, lines[MID_END], columns[MID_END])
      ));
      right.add(new TextRange(
          DiffUtil.getOffset(rightDocument, lines[RIGHT_START], columns[RIGHT_START]),
          DiffUtil.getOffset(rightDocument, lines[RIGHT_END], columns[RIGHT_END])
      ));
    }

    return new MergeInnerDifferences(left, mid, right);
  }

  private static int getMaxLine(String text) {
    return text.split("\r\n|\r|\n").length + 1;
  }

  /**
   * Returns coderange in a LineFragment object.
   * This object allows highlighting in the IDEA diff window.
   *
   * @return LineFragment
   */
  public LineFragment getTwoSidedRange(String leftText, String rightText) {
    int maxLineLeft = getMaxLine(leftText);
    int maxLineRight = getMaxLine(rightText);
    lines[RIGHT_END] = lines[RIGHT_END] < 0 ? maxLineLeft : lines[RIGHT_END];
    lines[LEFT_END] = lines[LEFT_END] < 0 ? maxLineRight : lines[LEFT_END];
    List<DiffFragment> fragments =
        offsets.stream().map(RefactoringOffset::toDiffFragment).collect(Collectors.toList());
    Document leftDocument = new DocumentImpl(leftText);
    Document rightDocument = new DocumentImpl(leftText);
    if (hasColumns) {
      fragments.add(new DiffFragmentImpl(
          DiffUtil.getOffset(leftDocument, lines[LEFT_START], columns[LEFT_START]),
          DiffUtil.getOffset(leftDocument, lines[LEFT_END], columns[LEFT_END]),
          DiffUtil.getOffset(rightDocument, lines[RIGHT_START], columns[RIGHT_START]),
          DiffUtil.getOffset(rightDocument, lines[RIGHT_END], columns[RIGHT_END])));
    }
    return new LineFragmentImpl(lines[LEFT_START], lines[LEFT_END], lines[RIGHT_START],
        lines[RIGHT_END], 0, 0, 0, 0, fragments);
  }

  /**
   * Three sided range.
   */
  public SimpleThreesideDiffChange getThreeSidedRange(String leftText, String midText,
                                                      String rightText,
                                                      SimpleThreesideDiffViewer viewer) {
    int maxLineLeft = getMaxLine(leftText);
    int maxLineMid = getMaxLine(midText);
    int maxLineRight = getMaxLine(rightText);
    lines[RIGHT_END] = lines[RIGHT_END] < 0 ? maxLineLeft : lines[RIGHT_END];
    lines[MID_END] = lines[MID_END] < 0 ? maxLineMid : lines[MID_END];
    lines[LEFT_END] = lines[LEFT_END] < 0 ? maxLineRight : lines[LEFT_END];
    MergeLineFragment line =
        new MergeLineFragmentImpl(lines[LEFT_START], lines[LEFT_END], lines[MID_START],
            lines[MID_END], lines[RIGHT_START], lines[RIGHT_END]);
    return new SimpleThreesideDiffChange(line, getConflictType(type),
        toMergeInnerDifferences(leftText, midText, rightText), viewer);
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

}

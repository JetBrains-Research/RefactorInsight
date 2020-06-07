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
import com.intellij.diff.util.MergeConflictType;
import com.intellij.diff.util.TextDiffType;
import com.intellij.diff.util.ThreeSide;
import com.intellij.openapi.util.TextRange;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import utils.Utils;

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
  private VisualisationType type;
  private boolean hasColumns = false;

  private String[] lazyNames;
  private boolean lazy = false;

  /**
   * Data holder for three sided refactoring diff.
   */
  public RefactoringLine(CodeRange left, CodeRange mid, CodeRange right,
                         VisualisationType type, MarkingOption option, boolean hasColumns) {
    lines[LEFT_START] = left.getStartLine() - 1;
    lines[LEFT_END] = left.getEndLine();
    lines[RIGHT_START] = right.getStartLine() - 1;
    lines[RIGHT_END] = right.getEndLine();
    this.hasColumns = hasColumns;
    if (hasColumns) {
      columns[LEFT_START] = Math.max(left.getStartColumn(), 1);
      columns[LEFT_END] = Math.max(left.getEndColumn(), 1);
      columns[RIGHT_START] = Math.max(right.getStartColumn(), 1);
      columns[RIGHT_END] = Math.max(right.getEndColumn(), 1);
    }

    if (mid != null) {
      lines[MID_START] = mid.getStartLine() - 1;
      lines[MID_END] = mid.getEndLine();
      if (hasColumns) {
        columns[MID_START] = Math.max(mid.getStartColumn(), 1);
        columns[MID_END] = Math.max(mid.getEndColumn(), 1);
      }
    }
    processOption(mid, option);

    this.type = type;
  }

  /**
   * Generate Conflicttype from ThreeSidedType.
   * Conflict type is used to set highlighting colors and can disable
   * highlighting in the left or right editor.
   */
  public static MergeConflictType getConflictType(VisualisationType type) {
    switch (type) {
      case LEFT:
        return new MergeConflictType(TextDiffType.MODIFIED, true, false);
      case RIGHT:
        return new MergeConflictType(TextDiffType.INSERTED, false, true);
      default:
        return new MergeConflictType(TextDiffType.MODIFIED, true, true);
    }
  }

  public void setHasColumns(boolean hasColumns) {
    this.hasColumns = hasColumns;
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
      int leftStartOffset = Utils.getOffset(leftText, lines[LEFT_START] + 1, 1);
      left.add(new TextRange(
          Utils.getOffset(leftText, lines[LEFT_START] + 1, columns[LEFT_START]) - leftStartOffset,
          Utils.getOffset(leftText, lines[LEFT_END], columns[LEFT_END]) - leftStartOffset
      ));
      int midStartOffset = Utils.getOffset(midText, lines[MID_START] + 1, 1);
      mid.add(new TextRange(
          Utils.getOffset(midText, lines[MID_START] + 1, columns[MID_START]) - midStartOffset,
          Utils.getOffset(midText, lines[MID_END], columns[MID_END]) - midStartOffset
      ));
      int rightStartOffset = Utils.getOffset(rightText, lines[RIGHT_START] + 1, 1);
      right.add(new TextRange(
          Utils.getOffset(rightText, lines[RIGHT_START] + 1,
              columns[RIGHT_START]) - rightStartOffset,
          Utils.getOffset(rightText, lines[RIGHT_END],
              columns[RIGHT_END]) - rightStartOffset
      ));
    }
    return new MergeInnerDifferences(left, mid, right);
  }

  /**
   * Returns code range in a LineFragment object.
   * This object allows highlighting in the IDEA diff window.
   *
   * @return LineFragment
   */
  public LineFragment getTwoSidedRange(String leftText, String rightText) {
    correctLines(leftText, null, rightText);

    List<DiffFragment> fragments =
        offsets.stream().map(RefactoringOffset::toDiffFragment).collect(Collectors.toList());
    if (hasColumns) {
      fragments.add(new DiffFragmentImpl(
          Utils.getOffset(leftText, lines[LEFT_START] + 1, columns[LEFT_START]),
          Utils.getOffset(leftText, lines[LEFT_END], columns[LEFT_END]),
          Utils.getOffset(rightText, lines[RIGHT_START] + 1, columns[RIGHT_START]),
          Utils.getOffset(rightText, lines[RIGHT_END], columns[RIGHT_END])));
    }
    return new LineFragmentImpl(lines[LEFT_START], lines[LEFT_END], lines[RIGHT_START],
        lines[RIGHT_END], 0, 0, 0, 0, fragments);
  }

  /**
   * Three sided range.
   */
  public SimpleThreesideDiffChange getThreeSidedRange(SimpleThreesideDiffViewer viewer) {
    String leftText = viewer.getContent(ThreeSide.LEFT).getDocument().getText();
    String midText = viewer.getContent(ThreeSide.BASE).getDocument().getText();
    String rightText = viewer.getContent(ThreeSide.RIGHT).getDocument().getText();

    correctLines(leftText, midText, rightText);

    MergeLineFragment line = new MergeLineFragmentImpl(
        lines[LEFT_START], lines[LEFT_END],
        lines[MID_START], lines[MID_END],
        lines[RIGHT_START], lines[RIGHT_END]);

    return new SimpleThreesideDiffChange(line, getConflictType(type),
        toMergeInnerDifferences(leftText, midText, rightText), viewer);
  }

  private void correctLines(String leftText, String midText, String rightText) {
    int maxLineLeft = Utils.getMaxLine(leftText);
    int maxLineRight = Utils.getMaxLine(rightText);
    lines[RIGHT_END] = lines[RIGHT_END] < 0 ? maxLineRight : lines[RIGHT_END];
    lines[LEFT_END] = lines[LEFT_END] < 0 ? maxLineLeft : lines[LEFT_END];
    if (midText != null) {
      int maxLineMid = Utils.getMaxLine(midText);
      lines[MID_END] = lines[MID_END] < 0 ? maxLineMid : lines[MID_END];
    }
    lazy(leftText, midText, rightText);
  }

  /**
   * Adds offsets to offset list.
   *
   * @param left  Location info
   * @param right Location info
   * @return this
   */
  public RefactoringLine addOffset(LocationInfo left, LocationInfo right) {
    offsets.add(new RefactoringOffset(left.getStartOffset(), left.getEndOffset(),
        right.getStartOffset(), right.getEndOffset()));
    return this;
  }

  /**
   * Adds offsets to offset list.
   *
   * @param location Location info of the offset
   * @param option   ADD for addition, REMOVE for removals
   * @return this
   */
  public RefactoringLine addOffset(LocationInfo location, MarkingOption option) {
    int beforeStart = location.getStartOffset();
    int beforeEnd = location.getEndOffset();
    int afterStart = location.getStartOffset();
    int afterEnd = location.getEndOffset();
    switch (option) {
      case ADD:
        beforeStart = beforeEnd = 0;
        break;
      case REMOVE:
        afterStart = afterEnd = 0;
        break;
      default:
    }
    offsets.add(new RefactoringOffset(beforeStart, beforeEnd, afterStart, afterEnd));
    return this;
  }

  /**
   * Adds offsets to offset list.
   *
   * @param left  Location info
   * @param mid   Location info
   * @param right Location info
   * @return this
   */
  public RefactoringLine addOffset(LocationInfo left, LocationInfo mid, LocationInfo right) {
    offsets
        .add(new RefactoringOffset(left.getStartOffset(), left.getEndOffset(), mid.getStartOffset(),
            mid.getEndOffset(), right.getStartOffset(), right.getEndOffset()));
    return this;
  }

  public void setLazyNames(String[] lazyNames) {
    this.lazyNames = lazyNames;
    lazy = true;
  }

  public int getRightStart() {
    return lines[RIGHT_START];
  }

  private void lazy(String leftText, String midText, String rightText) {
    if (!lazy) {
      return;
    }
    this.hasColumns = true;
    this.columns = new int[]{1, 1, 0, 0, columns[RIGHT_START], columns[RIGHT_END]};
    if(midText == null) {
      int[] beforeColumns =
          Utils.findColumns(leftText, lazyNames[0], lines[LEFT_START] + 1);

      int[] afterColumns =
          Utils.findColumns(rightText, lazyNames[2], lines[RIGHT_START] + 1);

      this.columns[LEFT_START] = beforeColumns[0];
      this.columns[LEFT_END] = beforeColumns[1];
      this.columns[RIGHT_START] = afterColumns[0];
      this.columns[RIGHT_END] = afterColumns[1];
    } else {
      int[] midColumns =
          Utils.findColumns(midText, lazyNames[1], lines[MID_START] + 1);

      this.columns[MID_START] = midColumns[0];
      this.columns[MID_END] = midColumns[1];
    }
    for (int i = 0; i < columns.length; i++) {
      columns[i] = Math.max(columns[i], 1);
    }
  }

  private void processOption(CodeRange mid, MarkingOption option) {
    switch (option) {
      case ADD:
        lines[LEFT_END] = lines[LEFT_START];
        break;
      case REMOVE:
        lines[RIGHT_END] = lines[RIGHT_START];
        break;
      case COLLAPSE:
        lines[LEFT_END] = lines[LEFT_START] + 1;
        lines[RIGHT_END] = lines[RIGHT_START] + 1;
        break;
      case EXTRACT:
        lines[LEFT_START] = 0;
        lines[LEFT_END] = 1;
        if (mid != null) {
          lines[MID_END] = lines[MID_START] + 1;
        }
        break;
      default:
    }
  }

  public enum VisualisationType {
    LEFT,
    RIGHT,
    BOTH,
    TWO
  }

  public enum MarkingOption {
    ADD,
    REMOVE,
    COLLAPSE,
    NONE,
    EXTRACT
  }
}

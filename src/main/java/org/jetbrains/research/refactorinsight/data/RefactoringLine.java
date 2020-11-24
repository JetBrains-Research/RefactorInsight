package org.jetbrains.research.refactorinsight.data;

import com.intellij.diff.fragments.DiffFragment;
import com.intellij.diff.fragments.DiffFragmentImpl;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.fragments.MergeLineFragmentImpl;
import com.intellij.openapi.util.TextRange;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.research.refactorinsight.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.adapters.LocationInfo;
import org.jetbrains.research.refactorinsight.data.diff.MoreSidedDiffRequestGenerator;
import org.jetbrains.research.refactorinsight.data.diff.ThreeSidedRange;
import org.jetbrains.research.refactorinsight.utils.Utils;

/**
 * RefactoringLine holds the data needed in order to create a LineFragment,
 * or a ThreeSidedRange that are used for the diff window.
 * It is a processing class.
 * It retrieves information about RefactoringMiner's code ranges and
 * it corrects and modify the data such that it can be displayed in a diff window.
 */
public class RefactoringLine {

  private static final int LEFT_START = 0;
  private static final int LEFT_END = 1;
  private static final int MID_START = 2;
  private static final int MID_END = 3;
  private static final int RIGHT_START = 4;
  private static final int RIGHT_END = 5;
  VisualisationType type;
  private final int[] lines = new int[6];
  private int[] columns = new int[6];
  private final List<RefactoringOffset> offsets = new ArrayList<>();
  private boolean hasColumns = false;
  private String[] word;
  private boolean lazy = false;
  private List<TextRange> left;
  private List<TextRange> mid;
  private List<TextRange> right;
  private LineFragmentImpl fragment;
  private MoreSidedDiffRequestGenerator.MoreSidedRange moreSidedRange;
  private final MarkingOption markingOption;
  private boolean moreSided;

  /**
   * Data holder for three sided refactoring diff.
   */
  public RefactoringLine(CodeRange left, CodeRange mid, CodeRange right,
                         VisualisationType type, MarkingOption option, boolean hasColumns) {
    this(left, mid, right, type, option, hasColumns, false);
  }

  /**
   * Data holder for three sided refactoring diff.
   */
  public RefactoringLine(CodeRange left, CodeRange mid, CodeRange right,
                         VisualisationType type, MarkingOption option,
                         boolean hasColumns, boolean moreSided) {
    this.markingOption = option;
    this.type = type;
    this.moreSided = moreSided;
    processLinesAndCols(left, mid, right, hasColumns);
  }

  /**
   * Corrects lines and offsets returned by RefactoringMiner.
   *
   * @param leftText  String containing whole left file contents
   * @param midText   String containing whole middle file contents
   * @param rightText String containing whole right file contents
   */
  public void correctLines(String leftText, String midText, String rightText,
                           boolean skipAnnotationsLeft, boolean skipAnnotationsMid,
                           boolean skipAnnotationsRight) {
    if (leftText != null) {
      int maxLineLeft = Utils.getMaxLine(leftText);
      lines[LEFT_END] =
          lines[LEFT_END] < 0 || lines[LEFT_END] > maxLineLeft ? maxLineLeft : lines[LEFT_END];
      lines[LEFT_START] =
          lines[LEFT_START] < 0 || lines[LEFT_START] > maxLineLeft ? 0 : lines[LEFT_START];
      lines[LEFT_START] = Utils.skipJavadoc(leftText, lines[LEFT_START], skipAnnotationsLeft);
    }

    if (midText != null) {
      int maxLineMid = Utils.getMaxLine(midText);
      lines[MID_END] =
          lines[MID_END] < 0 || lines[MID_END] > maxLineMid ? maxLineMid : lines[MID_END];
      lines[MID_START] =
          lines[MID_START] < 0 || lines[MID_START] > maxLineMid ? 0 : lines[MID_START];
      lines[MID_START] = Utils.skipJavadoc(midText, lines[MID_START], skipAnnotationsMid);

    }

    if (rightText != null) {
      int maxLineRight = Utils.getMaxLine(rightText);
      lines[RIGHT_END] =
          lines[RIGHT_END] < 0 || lines[RIGHT_END] > maxLineRight ? maxLineRight : lines[RIGHT_END];
      lines[RIGHT_START] =
          lines[RIGHT_START] < 0 || lines[RIGHT_START] > maxLineRight ? 0 : lines[RIGHT_START];
      lines[RIGHT_START] = Utils.skipJavadoc(rightText, lines[RIGHT_START], skipAnnotationsRight);
    }

    if (markingOption == MarkingOption.PACKAGE) {
      highlightPackage(leftText, rightText);
    }
    processOption(midText != null, markingOption);
    computeHighlighting(leftText, midText, rightText);
    if (moreSided) {
      computeMoreSidedRanges(leftText, rightText);
    }
    if (midText == null) {
      computeTwoSidedRanges(leftText, rightText);
    } else {
      computeThreeSidedRanges(leftText, midText, rightText);
    }
  }

  private void computeMoreSidedRanges(String leftText, String rightText) {
    MoreSidedDiffRequestGenerator.MoreSidedRange
        moreSidedRange = new MoreSidedDiffRequestGenerator.MoreSidedRange();
    moreSidedRange.startLineLeft = lines[LEFT_START] + 1;
    moreSidedRange.endLineLeft = lines[LEFT_END];
    moreSidedRange.startOffsetLeft = Utils
        .getOffset(leftText, lines[LEFT_START] + 1, columns[LEFT_START]);
    moreSidedRange.endOffsetLeft = Utils.getOffset(leftText, lines[LEFT_END], columns[LEFT_END]);

    moreSidedRange.startLineRight = lines[RIGHT_START] + 1;
    moreSidedRange.endLineRight = lines[RIGHT_END];
    moreSidedRange.startOffsetRight =
        Utils.getOffset(rightText, lines[RIGHT_START] + 1, columns[RIGHT_START]);
    moreSidedRange.endOffsetRight = Utils
        .getOffset(rightText, lines[RIGHT_END], columns[RIGHT_END]);
    this.moreSidedRange = moreSidedRange;
  }

  private void computeThreeSidedRanges(String leftText, String midText, String rightText) {
    left = offsets.stream().map(RefactoringOffset::getLeftRange)
        .collect(Collectors.toList());
    mid = new ArrayList<>();
    right = offsets.stream().map(RefactoringOffset::getRightRange)
        .collect(Collectors.toList());

    if (hasColumns) {
      try {
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
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void computeTwoSidedRanges(String leftText, String rightText) {
    if (lines[LEFT_START] == lines[LEFT_END]
        && lines[RIGHT_START] == lines[RIGHT_END]) {
      return;
    }
    List<DiffFragment> fragments = offsets.stream().map(RefactoringOffset::toDiffFragment)
        .collect(Collectors.toList());
    if (hasColumns) {
      int leftStart = lines[LEFT_START] == lines[LEFT_END]
          ? lines[LEFT_START] : lines[LEFT_START] + 1;
      int rightStart = lines[RIGHT_START] == lines[RIGHT_END]
          ? lines[RIGHT_START] : lines[RIGHT_START] + 1;
      fragments.add(new DiffFragmentImpl(
          Utils.getOffset(leftText, leftStart, columns[LEFT_START]),
          Utils.getOffset(leftText, lines[LEFT_END], columns[LEFT_END]),
          Utils.getOffset(rightText, rightStart, columns[RIGHT_START]),
          Utils.getOffset(rightText, lines[RIGHT_END], columns[RIGHT_END])));
    }
    fragment = new LineFragmentImpl(lines[LEFT_START], lines[LEFT_END], lines[RIGHT_START],
        lines[RIGHT_END], 0, 0, 0, 0, fragments);
  }

  private void computeHighlighting(String leftText, String midText, String rightText) {
    if (!lazy) {
      return;
    }
    hasColumns = true;
    columns = new int[]{1, 1, 0, 0, columns[RIGHT_START], columns[RIGHT_END]};
    if (word[0] != null && word[1] == null && word[2] == null) {
      int[] beforeColumns =
          Utils.findColumnsBackwards(leftText, word[0], lines[LEFT_START]);
      columns[LEFT_START] = beforeColumns[0];
      columns[LEFT_END] = beforeColumns[1];
    } else {
      if (word[0] != null) {
        int[] beforeColumns =
            Utils.findColumns(leftText, word[0], lines[LEFT_START]);
        columns[LEFT_START] = beforeColumns[0];
        columns[LEFT_END] = beforeColumns[1];
      }
      if (word[1] != null && midText != null) {
        int[] midColumns =
            Utils.findColumns(midText, word[1], lines[MID_START]);

        columns[MID_START] = midColumns[0];
        columns[MID_END] = midColumns[1];
      }
      if (word[2] != null) {
        int[] afterColumns =
            Utils.findColumns(rightText, word[2], lines[RIGHT_START]);
        columns[RIGHT_START] = afterColumns[0];
        columns[RIGHT_END] = afterColumns[1];
      }
    }
    for (int i = 0; i < columns.length; i++) {
      columns[i] = Math.max(columns[i], 1);
    }
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

  public LineFragmentImpl getTwoSidedRange() {
    return fragment;
  }

  /**
   * Computes the ThreeSidedRange representing this.
   *
   * @return ThreeSidedRange
   */
  public ThreeSidedRange getThreeSidedRange() {
    return new ThreeSidedRange(left, mid, right, type,
        new MergeLineFragmentImpl(
            lines[LEFT_START], lines[LEFT_END],
            lines[MID_START], lines[MID_END],
            lines[RIGHT_START], lines[RIGHT_END]
        )
    );
  }

  public int getRightStart() {
    return lines[RIGHT_START];
  }

  public void setHasColumns(boolean hasColumns) {
    this.hasColumns = hasColumns;
  }

  public void setWord(String[] word) {
    this.word = word;
    lazy = true;
  }

  public void setMoreSided(boolean moreSided) {
    this.moreSided = moreSided;
  }

  public MoreSidedDiffRequestGenerator.MoreSidedRange getMoreSidedRange() {
    return moreSidedRange;
  }

  private void processOption(boolean hasMid, MarkingOption option) {
    switch (option) {
      case ADD:
        lines[LEFT_END] = lines[LEFT_START];
        columns[LEFT_END] = columns[LEFT_START];
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
        if (hasMid) {
          lines[MID_END] = lines[MID_START] + 1;
        }
        break;
      default:
    }
  }

  private void highlightPackage(String leftText, String rightText) {
    int packageLine1 = Utils.findPackageLine(leftText);
    int packageLine2 = Utils.findPackageLine(rightText);

    lines[LEFT_START] = packageLine1 == -1 ? 0 : packageLine1;
    lines[RIGHT_START] = packageLine2 == -1 ? 0 : packageLine2;
    lines[LEFT_END] = packageLine1 == -1 ? 0 : lines[LEFT_START] + 1;
    lines[RIGHT_END] = packageLine2 == -1 ? 0 : lines[RIGHT_START] + 1;
  }

  private void processLinesAndCols(CodeRange left, CodeRange mid, CodeRange right,
                                   boolean hasColumns) {
    this.hasColumns = hasColumns;
    if (left != null) {
      lines[LEFT_START] = left.getStartLine() - 1;
      lines[LEFT_END] = left.getEndLine();
      if (hasColumns) {
        columns[LEFT_START] = Math.max(left.getStartColumn(), 1);
        columns[LEFT_END] = Math.max(left.getEndColumn(), 1);
      }
    }
    if (mid != null) {
      lines[MID_START] = mid.getStartLine() - 1;
      lines[MID_END] = mid.getEndLine();
      if (hasColumns) {
        columns[MID_START] = Math.max(mid.getStartColumn(), 1);
        columns[MID_END] = Math.max(mid.getEndColumn(), 1);
      }
    }
    if (right != null) {
      lines[RIGHT_START] = right.getStartLine() - 1;
      lines[RIGHT_END] = right.getEndLine();
      if (hasColumns) {
        columns[RIGHT_START] = Math.max(right.getStartColumn(), 1);
        columns[RIGHT_END] = Math.max(right.getEndColumn(), 1);
      }
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
    EXTRACT,
    PACKAGE
  }

  public static class RefactoringOffset {

    private final int leftStart;
    private final int leftEnd;
    private final int rightStart;
    private final int rightEnd;

    /**
     * Sub-highlighting two sided.
     * These params are the character where to start and where to end
     *
     * @param leftStart  int
     * @param leftEnd    int
     * @param rightStart int
     * @param rightEnd   int
     */
    public RefactoringOffset(int leftStart, int leftEnd, int rightStart, int rightEnd) {
      this.leftStart = leftStart;
      this.leftEnd = leftEnd;
      this.rightStart = rightStart;
      this.rightEnd = rightEnd;
    }

    public DiffFragmentImpl toDiffFragment() {
      return new DiffFragmentImpl(leftStart, leftEnd, rightStart, rightEnd);
    }

    public TextRange getLeftRange() {
      return new TextRange(leftStart, leftEnd);
    }

    public TextRange getRightRange() {
      return new TextRange(rightStart, rightEnd);
    }

  }
}

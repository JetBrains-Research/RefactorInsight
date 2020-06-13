package data.diffRequests;

import com.intellij.diff.fragments.MergeLineFragment;
import com.intellij.diff.fragments.MergeLineFragmentImpl;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.util.text.MergeInnerDifferences;
import com.intellij.diff.util.MergeConflictType;
import com.intellij.diff.util.TextDiffType;
import com.intellij.diff.util.ThreeSide;
import com.intellij.openapi.util.TextRange;
import data.RefactoringLine.VisualisationType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import utils.Utils;

public class ThreeSidedRange {

  List<TextRange> left;
  List<TextRange> mid;
  List<TextRange> right;

  VisualisationType type;

  MergeLineFragment fragment;

  public ThreeSidedRange(List<TextRange> left, List<TextRange> mid, List<TextRange> right,
                         VisualisationType type,
                         MergeLineFragment fragment) {
    this.left = left;
    this.mid = mid;
    this.right = right;
    this.fragment = fragment;
    this.type = type;
  }

  public SimpleThreesideDiffChange getDiffChange(SimpleThreesideDiffViewer viewer) {
    return new SimpleThreesideDiffChange(fragment, getMergeConflictType(type),
        new MergeInnerDifferences(left, mid, right), viewer);
  }

  @Override
  public String toString() {
    return Stream.of(type.toString(),
        fragment.getStartLine(ThreeSide.LEFT),
        fragment.getEndLine(ThreeSide.LEFT),
        fragment.getStartLine(ThreeSide.BASE),
        fragment.getEndLine(ThreeSide.BASE),
        fragment.getStartLine(ThreeSide.RIGHT),
        fragment.getEndLine(ThreeSide.RIGHT),
        stringify(left),
        stringify(mid),
        stringify(right)
    ).map(String::valueOf).collect(Collectors.joining(Utils.FRAG_DELIMITER));
  }

  private String stringify(List<TextRange> list) {
    return list.stream().map(r -> r.getStartOffset() + Utils.RANGE_DELIMITER + r.getEndOffset())
        .collect(Collectors.joining(Utils.RANGE_DELIMITER));
  }

  private static List<TextRange> deStringify(String value) {
    String[] tokens = value.split(Utils.RANGE_DELIMITER);
    assert tokens.length % 2 == 0;
    return IntStream.range(0, tokens.length / 2).map(i -> i * 2).mapToObj(i ->
        new TextRange(Integer.parseInt(tokens[i]), Integer.parseInt(tokens[i + 1])))
        .collect(Collectors.toList());
  }

  public static ThreeSidedRange fromString(String value) {
    String[] tokens = value.split(Utils.FRAG_DELIMITER);
    return new ThreeSidedRange(
        deStringify(tokens[7]),
        deStringify(tokens[8]),
        deStringify(tokens[9]),
        VisualisationType.valueOf(tokens[0]),
        new MergeLineFragmentImpl(
            Integer.parseInt(tokens[1]),
            Integer.parseInt(tokens[2]),
            Integer.parseInt(tokens[3]),
            Integer.parseInt(tokens[4]),
            Integer.parseInt(tokens[5]),
            Integer.parseInt(tokens[6])
        ));
  }

  private MergeConflictType getMergeConflictType(VisualisationType type) {
    switch (type) {
      case LEFT:
        return new MergeConflictType(TextDiffType.MODIFIED, true, false);
      case RIGHT:
        return new MergeConflictType(TextDiffType.INSERTED, false, true);
      default:
        return new MergeConflictType(TextDiffType.MODIFIED, true, true);
    }
  }
}

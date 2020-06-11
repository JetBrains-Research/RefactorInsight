package data.diff;

import com.intellij.diff.fragments.MergeLineFragment;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.util.text.MergeInnerDifferences;
import com.intellij.diff.util.MergeConflictType;
import com.intellij.diff.util.TextDiffType;
import com.intellij.openapi.util.TextRange;
import data.RefactoringLine;
import java.util.List;

public class ThreeSidedRange {
  List<TextRange> left;
  List<TextRange> mid;
  List<TextRange> right;

  RefactoringLine.VisualisationType type;

  MergeLineFragment fragment;

  public ThreeSidedRange(List<TextRange> left, List<TextRange> mid, List<TextRange> right,
                         RefactoringLine.VisualisationType type,
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

  private MergeConflictType getMergeConflictType(RefactoringLine.VisualisationType type) {
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

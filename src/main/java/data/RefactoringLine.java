package data;

import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import gr.uom.java.xmi.LocationInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RefactoringLine {

  private int beforeStart;
  private int beforeEnd;
  private int afterStart;
  private int afterEnd;
  private List<RefactoringOffset> offsets = new ArrayList<>();

  /**
   * Data holder for refactoring code ranges.
   * @param beforeStart int
   * @param beforeEnd int
   * @param afterStart int
   * @param afterEnd int
   */
  public RefactoringLine(int beforeStart, int beforeEnd, int afterStart, int afterEnd) {
    this.beforeStart = beforeStart;
    this.beforeEnd = beforeEnd;
    this.afterStart = afterStart;
    this.afterEnd = afterEnd;
  }

  public RefactoringLine() {
  }

  /**
   * Returns coderange in a LineFragment object.
   * This object allows highlighting in the IDEA diff window.
   * @return LineFragment
   */
  public LineFragment toLineFragment() {
    if (offsets.isEmpty()) {
      return new LineFragmentImpl(beforeStart, beforeEnd, afterStart, afterEnd,
          0, 0, 0, 0);
    } else {
      return new LineFragmentImpl(beforeStart, beforeEnd, afterStart, afterEnd,
          0, 0, 0, 0,
          offsets.stream().map(RefactoringOffset::toDiffFragment).collect(Collectors.toList()));
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

}

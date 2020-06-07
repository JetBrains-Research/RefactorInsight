package data;

import com.intellij.diff.fragments.DiffFragmentImpl;
import com.intellij.openapi.util.TextRange;

public class RefactoringOffset {

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

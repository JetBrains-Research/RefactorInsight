package data;

import com.intellij.diff.fragments.DiffFragmentImpl;

public class RefactoringOffset {

  private int beforeStart;
  private int beforeEnd;
  private int afterStart;
  private int afterEnd;

  /**
   * Subhighlighting.
   * These params are the character where to start and where to end
   * @param beforeStart int
   * @param beforeEnd int
   * @param afterStart int
   * @param afterEnd int
   */
  public RefactoringOffset(int beforeStart, int beforeEnd, int afterStart, int afterEnd) {
    this.beforeStart = beforeStart;
    this.beforeEnd = beforeEnd;
    this.afterStart = afterStart;
    this.afterEnd = afterEnd;
  }

  public RefactoringOffset() {
  }

  public DiffFragmentImpl toDiffFragment() {
    return new DiffFragmentImpl(beforeStart, beforeEnd, afterStart, afterEnd);
  }

}

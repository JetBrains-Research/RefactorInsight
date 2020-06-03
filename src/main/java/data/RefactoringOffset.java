package data;

import com.intellij.diff.fragments.DiffFragmentImpl;
import com.intellij.openapi.util.TextRange;

public class RefactoringOffset {

  private int leftStart;
  private int leftEnd;
  private int midStart;
  private int midEnd;
  private int rightStart;
  private int rightEnd;

  /**
   * Subhighlighting two sided.
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

  /**
   * Subhighlighting three sided.
   * These params are the character where to start and where to end
   *
   * @param leftStart  int
   * @param leftEnd    int
   * @param midStart   int
   * @param midEnd     int
   * @param rightStart int
   * @param rightEnd   int
   */
  public RefactoringOffset(int leftStart, int leftEnd, int midStart, int midEnd, int rightStart,
                           int rightEnd) {
    this.leftStart = leftStart;
    this.leftEnd = leftEnd;
    this.midStart = midStart;
    this.midEnd = midEnd;
    this.rightStart = rightStart;
    this.rightEnd = rightEnd;
  }

  public RefactoringOffset() {
  }

  public DiffFragmentImpl toDiffFragment() {
    return new DiffFragmentImpl(leftStart, leftEnd, rightStart, rightEnd);
  }

  public TextRange getLeftRange() {
    return new TextRange(leftStart, leftEnd);
  }

  public TextRange getMidRange() {
    return new TextRange(midStart, midEnd);
  }

  public TextRange getRightRange() {
    return new TextRange(rightStart, rightEnd);
  }

}

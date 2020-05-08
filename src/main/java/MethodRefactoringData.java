import java.io.Serializable;
import org.refactoringminer.api.RefactoringType;

public class MethodRefactoringData implements Serializable {

  private RefactoringType type;
  private MethodData methodBefore;
  private MethodData methodAfter;
  private long timeOfCommit;

  /**
   * Constructor for the methods refactoring class.
   * @param type of the refactoring.
   * @param methodBefore method before the refactoring.
   * @param methodAfter method after the refactoring.
   */
  public MethodRefactoringData(RefactoringType type,
                               MethodData methodBefore, MethodData methodAfter, long timeOfCommit) {
    this.type = type;
    this.methodBefore = methodBefore;
    this.methodAfter = methodAfter;
    this.timeOfCommit = timeOfCommit;
  }

  public MethodData getMethodAfter() {
    return methodAfter;
  }

  public MethodData getMethodBefore() {
    return methodBefore;
  }

  public RefactoringType getType() {
    return type;
  }

  public long getTimeOfCommit() {
    return timeOfCommit;
  }

}

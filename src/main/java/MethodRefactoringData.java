import java.io.Serializable;
import org.refactoringminer.api.RefactoringType;

public class MethodRefactoringData implements Serializable {

  private RefactoringType type;
  private String methodBefore;
  private String methodAfter;
  private long timeOfCommit;

  /**
   * Constructor for the methods refactoring class.
   * @param type of the refactoring.
   * @param methodBefore method before the refactoring.
   * @param methodAfter method after the refactoring.
   */
  public MethodRefactoringData(RefactoringType type,
                               String methodBefore, String methodAfter, long timeOfCommit) {
    this.type = type;
    this.methodBefore = methodBefore;
    this.methodAfter = methodAfter;
    this.timeOfCommit = timeOfCommit;
  }

  public String getMethodAfter() {
    return methodAfter;
  }

  public String getMethodBefore() {
    return methodBefore;
  }

  public RefactoringType getType() {
    return type;
  }

  public long getTimeOfCommit() {
    return timeOfCommit;
  }

}

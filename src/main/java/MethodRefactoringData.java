import java.io.Serializable;
import org.refactoringminer.api.RefactoringType;

public class MethodRefactoringData implements Serializable {

  private RefactoringType type;
  private String methodBefore;
  private String methodAfter;

  /**
   * Constructor for the methods refactoring class.
   *
   * @param type         of the refactoring.
   * @param methodBefore method before the refactoring.
   * @param methodAfter  method after the refactoring.
   */
  public MethodRefactoringData(RefactoringType type,
                               String methodBefore, String methodAfter) {
    this.type = type;
    this.methodBefore = methodBefore;
    this.methodAfter = methodAfter;
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

}

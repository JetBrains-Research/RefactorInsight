import org.refactoringminer.api.RefactoringType;

public class MethodRefactoringData {

  private RefactoringType type;
  private MethodData methodBefore;
  private MethodData methodAfter;

  /**
   * Constructor for the methods refactoring class.
   * @param type of the refactoring.
   * @param methodBefore method before the refactoring.
   * @param methodAfter method after the refactoring.
   */
  public MethodRefactoringData(RefactoringType type,
                               MethodData methodBefore, MethodData methodAfter) {
    this.type = type;
    this.methodBefore = methodBefore;
    this.methodAfter = methodAfter;
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
}

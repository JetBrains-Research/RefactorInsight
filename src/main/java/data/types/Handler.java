package data.types;

import data.RefactoringInfo;
import gr.uom.java.xmi.UMLOperation;
import org.refactoringminer.api.Refactoring;

public abstract class Handler {

  /**
   * Builder for a method's signature.
   *
   * @param operation retrieved from RefactoringMiner
   * @return a String signature of the operation.
   */
  protected String calculateSignature(UMLOperation operation) {
    StringBuilder builder = new StringBuilder();
    builder.append(operation.getClassName())
        .append(".")
        .append(operation.getName())
        .append("(");
    operation.getParameterTypeList().forEach(x -> builder.append(x).append(", "));

    if (operation.getParameterTypeList().size() > 0) {
      builder.deleteCharAt(builder.length() - 1);
      builder.deleteCharAt(builder.length() - 1);
    }

    builder.append(")");
    return builder.toString();
  }

  /**
   * Start generating RefactoringInfo from Refactoring.
   *
   * @param refactoring Refactoring from RefactoringMiner
   * @return RefactoringInfo
   */
  public RefactoringInfo handle(Refactoring refactoring) {
    return specify(refactoring, new RefactoringInfo()
        .setType(refactoring.getRefactoringType())
        .setName(refactoring.getName()));
  }

  public abstract RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info);


}

package data.types;

import data.RefactoringInfo;
import data.TrueCodeRange;
import gr.uom.java.xmi.UMLOperation;
import java.util.List;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public interface Handler {

  /**
   * Builder for a method's signature.
   *
   * @param operation retrieved from RefactoringMiner
   * @return a String signature of the operation.
   */
  static String calculateSignature(UMLOperation operation) {
    StringBuilder builder = new StringBuilder();
    builder.append(operation.getClassName())
        .append(".")
        .append(operation.getName())
        .append("(");
    operation.getParameterTypeList().forEach(x -> builder.append(x).append(","));

    if (operation.getParameterTypeList().size() > 0) {
      builder.deleteCharAt(builder.length() - 1);
    }

    builder.append(")");
    return builder.toString();
  }


  RefactoringInfo handle(Refactoring refactoring, String commitId);

}

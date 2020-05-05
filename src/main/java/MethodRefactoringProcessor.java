import static org.refactoringminer.api.RefactoringType.MOVE_OPERATION;
import static org.refactoringminer.api.RefactoringType.PULL_UP_OPERATION;
import static org.refactoringminer.api.RefactoringType.PUSH_DOWN_OPERATION;
import static org.refactoringminer.api.RefactoringType.RENAME_METHOD;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.CodeRange;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import gr.uom.java.xmi.diff.PushDownOperationRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;


public class MethodRefactoringProcessor {

  private final String projectPath;
  private final Map<RefactoringType, Function<Refactoring, MethodRefactoringData>> handlers =
      new HashMap<RefactoringType, Function<Refactoring, MethodRefactoringData>>() {{
      put(RENAME_METHOD, new RenameMethodRefactoringHandler());
      put(MOVE_OPERATION, new MoveOperationRefactoringHandler());
      put(PULL_UP_OPERATION, new PullUpOperationRefactoringHandler());
      put(PUSH_DOWN_OPERATION, new PushDownOperationRefactoringHandler());
    }
  };

  public MethodRefactoringProcessor(String projectPath) {
    this.projectPath = projectPath;
  }

  /**
   * Calculates the siganture of a method,
   * including the packages names and
   * parameters types.
   * @param operation method
   * @return the signature of the method
   */
  public static String calculateSignature(UMLOperation operation) {
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

  public MethodRefactoringData process(Refactoring refactoring) {
    return handlers.getOrDefault(refactoring.getRefactoringType(),
      x -> null).apply(refactoring);
  }

  private class MoveOperationRefactoringHandler
          implements Function<Refactoring, MethodRefactoringData> {
    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;
      final CodeRange before = ref.getSourceOperationCodeRangeBeforeMove();
      final CodeRange after = ref.getTargetOperationCodeRangeAfterMove();
      return new MethodRefactoringData(MOVE_OPERATION,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getMovedOperation()),
                      after.getStartLine(), before.getEndLine()));
    }
  }

  private class PullUpOperationRefactoringHandler
          implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;
      final CodeRange before = ref.getSourceOperationCodeRangeBeforeMove();
      final CodeRange after = ref.getTargetOperationCodeRangeAfterMove();
      return new MethodRefactoringData(PULL_UP_OPERATION,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getMovedOperation()),
                      after.getStartLine(), before.getEndLine()));
    }
  }

  private class PushDownOperationRefactoringHandler
          implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final PushDownOperationRefactoring ref = (PushDownOperationRefactoring) refactoring;
      final CodeRange before = ref.getSourceOperationCodeRangeBeforeMove();
      final CodeRange after = ref.getTargetOperationCodeRangeAfterMove();
      return new MethodRefactoringData(PUSH_DOWN_OPERATION,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getMovedOperation()),
                      after.getStartLine(), before.getEndLine()));
    }
  }

  private class RenameMethodRefactoringHandler
          implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;
      final int startLineBefore = ref.getSourceOperationCodeRangeBeforeRename().getStartLine();
      final int endLineBefore = ref.getSourceOperationCodeRangeBeforeRename().getEndLine();
      final int startLine = ref.getTargetOperationCodeRangeAfterRename().getStartLine();
      final int endLine = ref.getTargetOperationCodeRangeAfterRename().getEndLine();
      return new MethodRefactoringData(RENAME_METHOD,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      startLineBefore, endLineBefore),
              new MethodData(calculateSignature(ref.getRenamedOperation()),
                      startLine, endLine));
    }
  }

}


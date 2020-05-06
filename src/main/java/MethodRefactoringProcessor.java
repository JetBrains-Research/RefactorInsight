import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.CodeRange;
import gr.uom.java.xmi.diff.ExtractAndMoveOperationRefactoring;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import gr.uom.java.xmi.diff.PushDownOperationRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class MethodRefactoringProcessor {

  private final String projectPath;
  private final Map<RefactoringType, BiFunction<Refactoring,
          Long, MethodRefactoringData>> handlers =
      new HashMap<RefactoringType, BiFunction<Refactoring, Long, MethodRefactoringData>>() {{
      put(RefactoringType.RENAME_METHOD, new RenameMethodRefactoringHandler());
      put(RefactoringType.MOVE_OPERATION, new MoveRefactoringHandler());
      put(RefactoringType.PULL_UP_OPERATION, new PullUpRefactoringHandler());
      put(RefactoringType.PUSH_DOWN_OPERATION, new PushDownRefactoringHandler());
      put(RefactoringType.EXTRACT_OPERATION, new ExtractRefactoringHandler());
      put(RefactoringType.EXTRACT_AND_MOVE_OPERATION, new ExtractAndMoveRefactoringHandler());
      put(RefactoringType.INLINE_OPERATION, new InLineRefactoringHandler());
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

  public MethodRefactoringData process(Refactoring refactoring, Long timeOfCommit) {
    return handlers.getOrDefault(refactoring.getRefactoringType(),
      (x, y) -> null).apply(refactoring, timeOfCommit);
  }

  private class MoveRefactoringHandler
          implements BiFunction<Refactoring, Long, MethodRefactoringData> {
    @Override
    public MethodRefactoringData apply(Refactoring refactoring, Long timeOfCommit) {
      final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;
      final CodeRange before = ref.getSourceOperationCodeRangeBeforeMove();
      final CodeRange after = ref.getTargetOperationCodeRangeAfterMove();
      return new MethodRefactoringData(RefactoringType.MOVE_OPERATION,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getMovedOperation()),
                      after.getStartLine(), before.getEndLine()), timeOfCommit);
    }
  }

  private class PullUpRefactoringHandler
          implements BiFunction<Refactoring, Long, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring, Long timeOfCommit) {
      final PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;
      final CodeRange before = ref.getSourceOperationCodeRangeBeforeMove();
      final CodeRange after = ref.getTargetOperationCodeRangeAfterMove();
      return new MethodRefactoringData(RefactoringType.PULL_UP_OPERATION,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getMovedOperation()),
                      after.getStartLine(), before.getEndLine()), timeOfCommit);
    }
  }

  private class PushDownRefactoringHandler
          implements BiFunction<Refactoring, Long, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring, Long timeOfCommit) {
      final PushDownOperationRefactoring ref = (PushDownOperationRefactoring) refactoring;
      final CodeRange before = ref.getSourceOperationCodeRangeBeforeMove();
      final CodeRange after = ref.getTargetOperationCodeRangeAfterMove();
      return new MethodRefactoringData(RefactoringType.PUSH_DOWN_OPERATION,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getMovedOperation()),
                      after.getStartLine(), before.getEndLine()), timeOfCommit);
    }
  }

  private class RenameMethodRefactoringHandler
          implements BiFunction<Refactoring, Long, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring, Long timeOfCommit) {
      final RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;
      final int startLineBefore = ref.getSourceOperationCodeRangeBeforeRename().getStartLine();
      final int endLineBefore = ref.getSourceOperationCodeRangeBeforeRename().getEndLine();
      final int startLine = ref.getTargetOperationCodeRangeAfterRename().getStartLine();
      final int endLine = ref.getTargetOperationCodeRangeAfterRename().getEndLine();
      return new MethodRefactoringData(RefactoringType.RENAME_METHOD,
              new MethodData(calculateSignature(ref.getOriginalOperation()),
                      startLineBefore, endLineBefore),
              new MethodData(calculateSignature(ref.getRenamedOperation()),
                      startLine, endLine), timeOfCommit);
    }
  }

  private class ExtractRefactoringHandler
          implements BiFunction<Refactoring, Long, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring, Long timeOfCommit) {
      final ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;
      CodeRange before = ref.getSourceOperationCodeRangeBeforeExtraction();
      CodeRange after  = ref.getSourceOperationCodeRangeAfterExtraction();
      return new MethodRefactoringData(RefactoringType.EXTRACT_OPERATION,
              new MethodData(calculateSignature(ref.getSourceOperationBeforeExtraction()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getSourceOperationAfterExtraction()),
                      after.getStartLine(), after.getEndLine()), timeOfCommit);
    }
  }

  private class ExtractAndMoveRefactoringHandler
          implements BiFunction<Refactoring, Long, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring, Long timeOfCommit) {
      final ExtractAndMoveOperationRefactoring ref = (ExtractAndMoveOperationRefactoring)
              refactoring;
      CodeRange before = ref.getSourceOperationBeforeExtraction().codeRange();
      CodeRange after = ref.getSourceOperationAfterExtraction().codeRange();
      return new MethodRefactoringData(RefactoringType.EXTRACT_AND_MOVE_OPERATION,
              new MethodData(calculateSignature(ref.getSourceOperationBeforeExtraction()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getSourceOperationAfterExtraction()),
                      after.getStartLine(), after.getEndLine()), timeOfCommit);
    }
  }

  private class InLineRefactoringHandler
          implements BiFunction<Refactoring, Long, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring, Long timeOfCommit) {
      final InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;
      CodeRange before = ref.getTargetOperationBeforeInline().codeRange();
      CodeRange after = ref.getTargetOperationAfterInline().codeRange();
      return new MethodRefactoringData(RefactoringType.INLINE_OPERATION,
              new MethodData(calculateSignature(ref.getTargetOperationBeforeInline()),
                      before.getStartLine(), before.getEndLine()),
              new MethodData(calculateSignature(ref.getTargetOperationAfterInline()),
                      after.getStartLine(), after.getEndLine()), timeOfCommit);
    }
  }

}


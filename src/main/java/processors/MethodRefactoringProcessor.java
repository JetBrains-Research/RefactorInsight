package processors;

import data.MethodRefactoringData;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.AddMethodAnnotationRefactoring;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import gr.uom.java.xmi.diff.ModifyMethodAnnotationRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import gr.uom.java.xmi.diff.PushDownOperationRefactoring;
import gr.uom.java.xmi.diff.RemoveMethodAnnotationRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class MethodRefactoringProcessor {

  private final String projectPath;
  private final Map<RefactoringType, Function<Refactoring, MethodRefactoringData>> handlers =
      new HashMap<>() {
        {
          put(RefactoringType.RENAME_METHOD, new RenameMethodRefactoringHandler());
          put(RefactoringType.MOVE_OPERATION, new MoveRefactoringHandler());
          put(RefactoringType.PULL_UP_OPERATION, new PullUpRefactoringHandler());
          put(RefactoringType.PUSH_DOWN_OPERATION, new PushDownRefactoringHandler());
          put(RefactoringType.EXTRACT_OPERATION, new ExtractRefactoringHandler());
          put(RefactoringType.EXTRACT_AND_MOVE_OPERATION, new ExtractRefactoringHandler());
          put(RefactoringType.INLINE_OPERATION, new InLineRefactoringHandler());
          put(RefactoringType.MOVE_AND_RENAME_OPERATION, new MoveRefactoringHandler());
          put(RefactoringType.MOVE_AND_INLINE_OPERATION, new InLineRefactoringHandler());
          put(RefactoringType.ADD_METHOD_ANNOTATION, new AddAnnotationRefactoringHandler());
          put(RefactoringType.REMOVE_METHOD_ANNOTATION, new RemoveAnnotationRefactoringHandler());
          put(RefactoringType.MODIFY_METHOD_ANNOTATION, new ModifyAnnotationRefactoringHandler());
        }
      };

  public MethodRefactoringProcessor(String projectPath) {
    this.projectPath = projectPath;
  }

  /**
   * Calculates the siganture of a method,
   * including the packages names and
   * parameters types.
   *
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
        (x) -> null).apply(refactoring);
  }

  private class MoveRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {
    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final MoveOperationRefactoring ref = (MoveOperationRefactoring) refactoring;
      if (!ref.getOriginalOperation().getName().equals(ref.getMovedOperation().getName())) {
        return new MethodRefactoringData(RefactoringType.MOVE_AND_RENAME_OPERATION,
            calculateSignature(ref.getOriginalOperation()),
            calculateSignature(ref.getMovedOperation()));
      } else {
        return new MethodRefactoringData(RefactoringType.MOVE_OPERATION,
            calculateSignature(ref.getOriginalOperation()),
            calculateSignature(ref.getMovedOperation()));
      }
    }
  }

  private class PullUpRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;
      return new MethodRefactoringData(RefactoringType.PULL_UP_OPERATION,
          calculateSignature(ref.getOriginalOperation()),
          calculateSignature(ref.getMovedOperation()));
    }
  }

  private class PushDownRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final PushDownOperationRefactoring ref = (PushDownOperationRefactoring) refactoring;
      return new MethodRefactoringData(RefactoringType.PUSH_DOWN_OPERATION,
          calculateSignature(ref.getOriginalOperation()),
          calculateSignature(ref.getMovedOperation()));
    }
  }

  private class RenameMethodRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;
      return new MethodRefactoringData(RefactoringType.RENAME_METHOD,
          calculateSignature(ref.getOriginalOperation()),
          calculateSignature(ref.getRenamedOperation()));
    }
  }

  private class ExtractRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final ExtractOperationRefactoring ref = (ExtractOperationRefactoring) refactoring;
      if (!ref.getSourceOperationBeforeExtraction().getClassName().equals(ref
          .getExtractedOperation().getClassName())) {
        return new MethodRefactoringData(RefactoringType.EXTRACT_AND_MOVE_OPERATION,
            calculateSignature(ref.getSourceOperationBeforeExtraction()),
            calculateSignature(ref.getSourceOperationAfterExtraction()));
      } else {
        return new MethodRefactoringData(RefactoringType.EXTRACT_OPERATION,
            calculateSignature(ref.getSourceOperationBeforeExtraction()),
            calculateSignature(ref.getSourceOperationAfterExtraction()));
      }
    }
  }

  private class InLineRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;
      if (!ref.getTargetOperationBeforeInline().getClassName().equals(ref
          .getTargetOperationAfterInline().getClassName())) {
        return new MethodRefactoringData(RefactoringType.MOVE_AND_INLINE_OPERATION,
            calculateSignature(ref.getTargetOperationBeforeInline()),
            calculateSignature(ref.getTargetOperationAfterInline()));
      } else {
        return new MethodRefactoringData(RefactoringType.INLINE_OPERATION,
            calculateSignature(ref.getTargetOperationBeforeInline()),
            calculateSignature(ref.getTargetOperationAfterInline()));
      }
    }
  }

  private class AddAnnotationRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final AddMethodAnnotationRefactoring ref = (AddMethodAnnotationRefactoring) refactoring;
      return new MethodRefactoringData(RefactoringType.ADD_METHOD_ANNOTATION,
          calculateSignature(ref.getOperationBefore()),
          calculateSignature(ref.getOperationAfter()));

    }
  }

  private class RemoveAnnotationRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final RemoveMethodAnnotationRefactoring ref = (RemoveMethodAnnotationRefactoring) refactoring;
      return new MethodRefactoringData(RefactoringType.REMOVE_METHOD_ANNOTATION,
          calculateSignature(ref.getOperationBefore()),
          calculateSignature(ref.getOperationAfter()));

    }
  }

  private class ModifyAnnotationRefactoringHandler
      implements Function<Refactoring, MethodRefactoringData> {

    @Override
    public MethodRefactoringData apply(Refactoring refactoring) {
      final ModifyMethodAnnotationRefactoring ref = (ModifyMethodAnnotationRefactoring) refactoring;
      return new MethodRefactoringData(RefactoringType.REMOVE_METHOD_ANNOTATION,
          calculateSignature(ref.getOperationBefore()),
          calculateSignature(ref.getOperationAfter()));

    }
  }

}


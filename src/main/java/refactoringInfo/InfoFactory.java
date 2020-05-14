package refactoringInfo;

import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import refactoringInfo.types.*;

import java.util.HashMap;
import java.util.Map;

public class InfoFactory {

  private Map<RefactoringType, Handler> refactoringHandlers = new HashMap<>();

  public InfoFactory() {
    refactoringHandlers.put(RefactoringType.CHANGE_ATTRIBUTE_TYPE, new ChangeAttributeTypeHandler());
    refactoringHandlers.put(RefactoringType.CHANGE_PARAMETER_TYPE, new ChangeParameterTypeHandler());
    refactoringHandlers.put(RefactoringType.CHANGE_RETURN_TYPE, new ChangeReturnTypeHandler());
    refactoringHandlers.put(RefactoringType.CHANGE_VARIABLE_TYPE, new ChangeVariableTypeHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_AND_MOVE_OPERATION, new ExtractAndMoveOperationHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_ATTRIBUTE, new ExtractAttributeHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_CLASS, new ExtractClassHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_INTERFACE, new ExtractInterfaceHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_OPERATION, new ExtractOperationHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_SUBCLASS, new ExtractSubClassHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_SUPERCLASS, new ExtractSuperClassHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_VARIABLE, new ExtractVariableHandler());
    refactoringHandlers.put(RefactoringType.INLINE_OPERATION, new InlineOperationHandler());
    refactoringHandlers.put(RefactoringType.INLINE_VARIABLE, new InlineVariableHandler());
    refactoringHandlers.put(RefactoringType.MERGE_ATTRIBUTE, new MergeAttributeHandler());
    refactoringHandlers.put(RefactoringType.MERGE_PARAMETER, new MergeParameterHandler());
    refactoringHandlers.put(RefactoringType.MERGE_VARIABLE, new MergeVariableHandler());
    refactoringHandlers.put(RefactoringType.MODIFY_METHOD_ANNOTATION, new ModifyMethodAnnotationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_AND_INLINE_OPERATION, new MoveAndInlineOperationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_AND_RENAME_OPERATION, new MoveAndRenameOperationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_ATTRIBUTE, new MoveAttributeHandler());
    refactoringHandlers.put(RefactoringType.MOVE_CLASS, new MoveClassHandler());
    refactoringHandlers.put(RefactoringType.MOVE_OPERATION, new MoveOperationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_RENAME_ATTRIBUTE, new MoveRenameAttributeHandler());
    refactoringHandlers.put(RefactoringType.MOVE_RENAME_CLASS, new MoveRenameClassHandler());
    refactoringHandlers.put(RefactoringType.MOVE_SOURCE_FOLDER, new MoveSourceFolderHandler());
    refactoringHandlers.put(RefactoringType.PARAMETERIZE_VARIABLE, new ParameterizeVariableHandler());
    refactoringHandlers.put(RefactoringType.PULL_UP_ATTRIBUTE, new PullUpAttributeHandler());
    refactoringHandlers.put(RefactoringType.PULL_UP_OPERATION, new PullUpOperationHandler());
    refactoringHandlers.put(RefactoringType.PUSH_DOWN_ATTRIBUTE, new PushDownAttributeHandler());
    refactoringHandlers.put(RefactoringType.PUSH_DOWN_OPERATION, new PushDownOperationHandler());
    refactoringHandlers.put(RefactoringType.REMOVE_METHOD_ANNOTATION, new RemoveMethodAnnotationHandler());
    refactoringHandlers.put(RefactoringType.RENAME_ATTRIBUTE, new RenameAttributeHandler());
    refactoringHandlers.put(RefactoringType.RENAME_CLASS, new RenameClassHandler());
    refactoringHandlers.put(RefactoringType.RENAME_METHOD, new RenameMethodHandler());
    refactoringHandlers.put(RefactoringType.RENAME_PACKAGE, new RenamePackageHandler());
    refactoringHandlers.put(RefactoringType.RENAME_PARAMETER, new RenameParameterHandler());
    refactoringHandlers.put(RefactoringType.RENAME_VARIABLE, new RenameVariableHandler());
    refactoringHandlers.put(RefactoringType.REPLACE_ATTRIBUTE, new ReplaceAttributeHandler());
    refactoringHandlers.put(RefactoringType.REPLACE_VARIABLE_WITH_ATTRIBUTE, new ReplaceVariableWithAttributeHandler());
    refactoringHandlers.put(RefactoringType.SPLIT_ATTRIBUTE, new SplitAttributeHandler());
    refactoringHandlers.put(RefactoringType.SPLIT_PARAMETER, new SplitParameterHandler());
    refactoringHandlers.put(RefactoringType.SPLIT_VARIABLE, new SplitVariableHandler());

  }

  public RefactoringInfo create(Refactoring refactoring) {

    return refactoringHandlers.get(refactoring.getRefactoringType()).handle(refactoring);
  }
}

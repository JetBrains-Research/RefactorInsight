package data;

import data.types.Handler;
import data.types.attributes.ChangeAttributeTypeHandler;
import data.types.attributes.ExtractAttributeHandler;
import data.types.attributes.MergeAttributeHandler;
import data.types.attributes.MoveAttributeHandler;
import data.types.attributes.MoveRenameAttributeHandler;
import data.types.attributes.PullUpAttributeHandler;
import data.types.attributes.PushDownAttributeHandler;
import data.types.attributes.RenameAttributeHandler;
import data.types.attributes.ReplaceAttributeHandler;
import data.types.attributes.SplitAttributeHandler;
import data.types.classes.ExtractClassHandler;
import data.types.classes.ExtractSuperClassHandler;
import data.types.classes.MoveClassHandler;
import data.types.classes.MoveRenameClassHandler;
import data.types.classes.RenameClassHandler;
import data.types.methods.AddMethodAnnotationHandler;
import data.types.methods.ChangeMethodSignatureHandler;
import data.types.methods.ExtractOperationHandler;
import data.types.methods.InlineOperationHandler;
import data.types.methods.MergeOperationHandler;
import data.types.methods.ModifyMethodAnnotationHandler;
import data.types.methods.MoveOperationHandler;
import data.types.methods.PullUpOperationHandler;
import data.types.methods.PushDownOperationHandler;
import data.types.methods.RemoveMethodAnnotationHandler;
import data.types.methods.RenameMethodHandler;
import data.types.packages.MoveSourceFolderHandler;
import data.types.packages.RenamePackageHandler;
import data.types.variables.ChangeReturnTypeHandler;
import data.types.variables.ChangeVariableTypeHandler;
import data.types.variables.ExtractVariableHandler;
import data.types.variables.InlineVariableHandler;
import data.types.variables.MergeVariableHandler;
import data.types.variables.RenameVariableHandler;
import data.types.variables.SplitVariableHandler;
import java.util.HashMap;
import java.util.Map;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class InfoFactory {

  private Map<RefactoringType, Handler> refactoringHandlers = new HashMap<>();

  /**
   * Constructor for the infoFactory.
   */
  public InfoFactory() {

    refactoringHandlers.put(RefactoringType.CHANGE_ATTRIBUTE_TYPE,
        new ChangeAttributeTypeHandler());
    refactoringHandlers.put(RefactoringType.CHANGE_PARAMETER_TYPE,
        new ChangeVariableTypeHandler());
    refactoringHandlers.put(RefactoringType.CHANGE_RETURN_TYPE,
        new ChangeReturnTypeHandler());
    refactoringHandlers.put(RefactoringType.CHANGE_VARIABLE_TYPE,
        new ChangeVariableTypeHandler());
    refactoringHandlers.put(RefactoringType.CHANGE_METHOD_SIGNATURE,
        new ChangeMethodSignatureHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_AND_MOVE_OPERATION,
        new ExtractOperationHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_ATTRIBUTE,
        new ExtractAttributeHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_CLASS,
        new ExtractClassHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_INTERFACE,
        new ExtractSuperClassHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_OPERATION,
        new ExtractOperationHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_SUBCLASS,
        new ExtractClassHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_SUPERCLASS,
        new ExtractSuperClassHandler());
    refactoringHandlers.put(RefactoringType.EXTRACT_VARIABLE,
        new ExtractVariableHandler());
    refactoringHandlers.put(RefactoringType.INLINE_OPERATION,
        new InlineOperationHandler());
    refactoringHandlers.put(RefactoringType.INLINE_VARIABLE,
        new InlineVariableHandler());
    refactoringHandlers.put(RefactoringType.MERGE_ATTRIBUTE,
        new MergeAttributeHandler());
    refactoringHandlers.put(RefactoringType.MERGE_PARAMETER,
        new MergeVariableHandler());
    refactoringHandlers.put(RefactoringType.MERGE_VARIABLE,
        new MergeVariableHandler());
    refactoringHandlers.put(RefactoringType.MERGE_OPERATION,
        new MergeOperationHandler());
    refactoringHandlers.put(RefactoringType.MODIFY_METHOD_ANNOTATION,
        new ModifyMethodAnnotationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_AND_INLINE_OPERATION,
        new InlineOperationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_AND_RENAME_OPERATION,
        new MoveOperationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_ATTRIBUTE,
        new MoveAttributeHandler());
    refactoringHandlers.put(RefactoringType.MOVE_CLASS,
        new MoveClassHandler());
    refactoringHandlers.put(RefactoringType.MOVE_OPERATION,
        new MoveOperationHandler());
    refactoringHandlers.put(RefactoringType.MOVE_RENAME_ATTRIBUTE,
        new MoveRenameAttributeHandler());
    refactoringHandlers.put(RefactoringType.MOVE_RENAME_CLASS,
        new MoveRenameClassHandler());
    refactoringHandlers.put(RefactoringType.MOVE_SOURCE_FOLDER,
        new MoveSourceFolderHandler());
    refactoringHandlers.put(RefactoringType.PARAMETERIZE_VARIABLE,
        new RenameVariableHandler());
    refactoringHandlers.put(RefactoringType.PULL_UP_ATTRIBUTE,
        new PullUpAttributeHandler());
    refactoringHandlers.put(RefactoringType.PULL_UP_OPERATION,
        new PullUpOperationHandler());
    refactoringHandlers.put(RefactoringType.PUSH_DOWN_ATTRIBUTE,
        new PushDownAttributeHandler());
    refactoringHandlers.put(RefactoringType.PUSH_DOWN_OPERATION,
        new PushDownOperationHandler());
    refactoringHandlers.put(RefactoringType.REMOVE_METHOD_ANNOTATION,
        new RemoveMethodAnnotationHandler());
    refactoringHandlers.put(RefactoringType.RENAME_ATTRIBUTE,
        new RenameAttributeHandler());
    refactoringHandlers.put(RefactoringType.RENAME_CLASS,
        new RenameClassHandler());
    refactoringHandlers.put(RefactoringType.RENAME_METHOD,
        new RenameMethodHandler());
    refactoringHandlers.put(RefactoringType.RENAME_PACKAGE,
        new RenamePackageHandler());
    refactoringHandlers.put(RefactoringType.RENAME_PARAMETER,
        new RenameVariableHandler());
    refactoringHandlers.put(RefactoringType.RENAME_VARIABLE,
        new RenameVariableHandler());
    refactoringHandlers.put(RefactoringType.REPLACE_ATTRIBUTE,
        new ReplaceAttributeHandler());
    refactoringHandlers.put(RefactoringType.REPLACE_VARIABLE_WITH_ATTRIBUTE,
        new RenameVariableHandler());
    refactoringHandlers.put(RefactoringType.SPLIT_ATTRIBUTE,
        new SplitAttributeHandler());
    refactoringHandlers.put(RefactoringType.SPLIT_PARAMETER,
        new SplitVariableHandler());
    refactoringHandlers.put(RefactoringType.SPLIT_VARIABLE,
        new SplitVariableHandler());
    refactoringHandlers.put(RefactoringType.ADD_METHOD_ANNOTATION,
        new AddMethodAnnotationHandler());

  }

  /**
   * Method that creates the relevant RefactoringInfo for a refactoring.
   *
   * @param refactoring to be analyzed
   * @return resulting RefactoringInfo
   */
  public RefactoringInfo create(Refactoring refactoring) {
    return refactoringHandlers.get(refactoring.getRefactoringType())
        .handle(refactoring);
  }
}

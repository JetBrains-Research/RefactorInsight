package org.jetbrains.research.refactorinsight.data;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.research.refactorinsight.data.types.Handler;
import org.jetbrains.research.refactorinsight.data.types.attributes.AddAttributeAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.ChangeAttributeTypeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.ExtractAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.MergeAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.ModifyAttributeAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.MoveAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.MoveRenameAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.PullUpAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.PushDownAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.RemoveAttributeAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.RenameAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.ReplaceAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.attributes.SplitAttributeHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.AddClassAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.ExtractClassHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.ExtractSuperClassHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.ModifyClassAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.MoveClassHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.MoveRenameClassHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.RemoveClassAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.classes.RenameClassHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.AddMethodAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.AddParameterHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ChangeReturnTypeHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ExtractOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.InlineOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.MergeOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ModifyMethodAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.MoveOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.PullUpOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.PushDownOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.RemoveMethodAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.RemoveParameterHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.RenameMethodHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ReorderParameterHandler;
import org.jetbrains.research.refactorinsight.data.types.packages.MoveSourceFolderHandler;
import org.jetbrains.research.refactorinsight.data.types.packages.RenamePackageHandler;
import org.jetbrains.research.refactorinsight.data.types.variables.ChangeVariableTypeHandler;
import org.jetbrains.research.refactorinsight.data.types.variables.ExtractVariableHandler;
import org.jetbrains.research.refactorinsight.data.types.variables.InlineVariableHandler;
import org.jetbrains.research.refactorinsight.data.types.variables.MergeVariableHandler;
import org.jetbrains.research.refactorinsight.data.types.variables.RenameVariableHandler;
import org.jetbrains.research.refactorinsight.data.types.variables.SplitVariableHandler;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

/**
 * Factory that creates RefactoringInfo objects given Refactoring objects
 * that were retrieved from RefactoringMiner.
 */
public class InfoFactory {

  private final Map<RefactoringType, Handler> refactoringHandlers = new HashMap<>();

  /**
   * Constructor for the info factory.
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
    refactoringHandlers.put(RefactoringType.ADD_PARAMETER,
        new AddParameterHandler());
    refactoringHandlers.put(RefactoringType.REMOVE_PARAMETER,
        new RemoveParameterHandler());
    refactoringHandlers.put(RefactoringType.ADD_ATTRIBUTE_ANNOTATION,
        new AddAttributeAnnotationHandler());
    refactoringHandlers.put(RefactoringType.REMOVE_ATTRIBUTE_ANNOTATION,
        new RemoveAttributeAnnotationHandler());
    refactoringHandlers.put(RefactoringType.MODIFY_ATTRIBUTE_ANNOTATION,
        new ModifyAttributeAnnotationHandler());
    refactoringHandlers.put(RefactoringType.ADD_CLASS_ANNOTATION,
        new AddClassAnnotationHandler());
    refactoringHandlers.put(RefactoringType.REMOVE_CLASS_ANNOTATION,
        new RemoveClassAnnotationHandler());
    refactoringHandlers.put(RefactoringType.MODIFY_CLASS_ANNOTATION,
        new ModifyClassAnnotationHandler());
    refactoringHandlers.put(RefactoringType.REORDER_PARAMETER,
        new ReorderParameterHandler());
  }

  /**
   * Method that creates the relevant RefactoringInfo for a given Refactoring.
   *
   * @param refactoring to be analyzed
   * @return resulting RefactoringInfo
   */
  public RefactoringInfo create(Refactoring refactoring, RefactoringEntry entry) {
    return refactoringHandlers.get(refactoring.getRefactoringType())
        .handle(refactoring, entry);
  }

}

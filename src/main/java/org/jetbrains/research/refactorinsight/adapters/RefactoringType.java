package org.jetbrains.research.refactorinsight.adapters;

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
import org.jetbrains.research.refactorinsight.data.types.methods.AddParameterAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.AddParameterHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ChangeReturnTypeHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ExtractOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.InlineOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.MergeOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ModifyMethodAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.ModifyParameterAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.MoveOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.PullUpOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.PushDownOperationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.RemoveMethodAnnotationHandler;
import org.jetbrains.research.refactorinsight.data.types.methods.RemoveParameterAnnotationHandler;
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

public enum RefactoringType {
  EXTRACT_OPERATION("Extract Method", new ExtractOperationHandler()),
  RENAME_CLASS("Rename Class", new RenameClassHandler()),
  MOVE_ATTRIBUTE("Move Attribute", new MoveAttributeHandler()),
  MOVE_RENAME_ATTRIBUTE("Move And Rename Attribute", new MoveRenameAttributeHandler()),
  REPLACE_ATTRIBUTE("Replace Attribute", new ReplaceAttributeHandler()),
  RENAME_METHOD("Rename Method", new RenameMethodHandler()),
  INLINE_OPERATION("Inline Method", new InlineOperationHandler()),
  MOVE_OPERATION("Move Method", new MoveOperationHandler()),
  MOVE_AND_RENAME_OPERATION("Move And Rename Method", new MoveOperationHandler()),
  PULL_UP_OPERATION("Pull Up Method", new PullUpOperationHandler()),
  MOVE_CLASS("Move Class", new MoveClassHandler()),
  MOVE_RENAME_CLASS("Move And Rename Class", new MoveRenameClassHandler()),
  MOVE_SOURCE_FOLDER("Move Source Folder", new MoveSourceFolderHandler()),
  PULL_UP_ATTRIBUTE("Pull Up Attribute", new PullUpAttributeHandler()),
  PUSH_DOWN_ATTRIBUTE("Push Down Attribute", new PushDownAttributeHandler()),
  PUSH_DOWN_OPERATION("Push Down Method", new PushDownOperationHandler()),
  EXTRACT_INTERFACE("Extract Interface", new ExtractSuperClassHandler()),
  EXTRACT_SUPERCLASS("Extract Superclass", new ExtractSuperClassHandler()),
  EXTRACT_SUBCLASS("Extract Subclass", new ExtractClassHandler()),
  EXTRACT_CLASS("Extract Class", new ExtractClassHandler()),
  MERGE_OPERATION("Merge Method", new MergeOperationHandler()),
  EXTRACT_AND_MOVE_OPERATION("Extract And Move Method", new ExtractOperationHandler()),
  MOVE_AND_INLINE_OPERATION("Move And Inline Method", new InlineOperationHandler()),
  RENAME_PACKAGE("Change Package", new RenamePackageHandler()),
  EXTRACT_VARIABLE("Extract Variable", new ExtractVariableHandler()),
  EXTRACT_ATTRIBUTE("Extract Attribute", new ExtractAttributeHandler()),
  INLINE_VARIABLE("Inline Variable", new InlineVariableHandler()),
  RENAME_VARIABLE("Rename Variable", new RenameVariableHandler()),
  RENAME_PARAMETER("Rename Parameter", new RenameVariableHandler()),
  RENAME_ATTRIBUTE("Rename Attribute", new RenameAttributeHandler()),
  MERGE_VARIABLE("Merge Variable", new MergeVariableHandler()),
  MERGE_PARAMETER("Merge Parameter", new MergeVariableHandler()),
  MERGE_ATTRIBUTE("Merge Attribute", new MergeAttributeHandler()),
  SPLIT_VARIABLE("Split Variable", new SplitVariableHandler()),
  SPLIT_PARAMETER("Split Parameter", new SplitVariableHandler()),
  SPLIT_ATTRIBUTE("Split Attribute", new SplitAttributeHandler()),
  REPLACE_VARIABLE_WITH_ATTRIBUTE("Replace Variable With Attribute", new RenameVariableHandler()),
  PARAMETERIZE_VARIABLE("Parameterize Variable", new RenameVariableHandler()),
  CHANGE_RETURN_TYPE("Change Return Type", new ChangeReturnTypeHandler()),
  CHANGE_VARIABLE_TYPE("Change Variable Type", new ChangeVariableTypeHandler()),
  CHANGE_PARAMETER_TYPE("Change Parameter Type", new ChangeVariableTypeHandler()),
  CHANGE_ATTRIBUTE_TYPE("Change Attribute Type", new ChangeAttributeTypeHandler()),
  ADD_METHOD_ANNOTATION("Add Method Annotation", new AddMethodAnnotationHandler()),
  REMOVE_METHOD_ANNOTATION("Remove Method Annotation", new RemoveMethodAnnotationHandler()),
  MODIFY_METHOD_ANNOTATION("Modify Method Annotation", new ModifyMethodAnnotationHandler()),
  ADD_ATTRIBUTE_ANNOTATION("Add Attribute Annotation", new AddAttributeAnnotationHandler()),
  REMOVE_ATTRIBUTE_ANNOTATION("Remove Attribute Annotation", new RemoveAttributeAnnotationHandler()),
  MODIFY_ATTRIBUTE_ANNOTATION("Modify Attribute Annotation", new ModifyAttributeAnnotationHandler()),
  ADD_CLASS_ANNOTATION("Add Class Annotation", new AddClassAnnotationHandler()),
  REMOVE_CLASS_ANNOTATION("Remove Class Annotation", new RemoveClassAnnotationHandler()),
  MODIFY_CLASS_ANNOTATION("Modify Class Annotation", new ModifyClassAnnotationHandler()),
  ADD_PARAMETER_ANNOTATION("Add Parameter Annotation", new AddParameterAnnotationHandler()),
  REMOVE_PARAMETER_ANNOTATION("Remove Parameter Annotation", new RemoveParameterAnnotationHandler()),
  MODIFY_PARAMETER_ANNOTATION("Modify Parameter Annotation", new ModifyParameterAnnotationHandler()),
  ADD_PARAMETER("Add Parameter", new AddParameterHandler()),
  REMOVE_PARAMETER("Remove Parameter", new RemoveParameterHandler()),
  REORDER_PARAMETER("Reorder Parameter", new ReorderParameterHandler());

  String name;
  Handler handler;

  RefactoringType(String name, Handler handler) {
    this.name = name;
    this.handler = handler;
  }

  public String getName() {
    return this.name;
  }

  public Handler getHandler() {
    return this.handler;
  }

  @Override
  public String toString() {
    return this.name;
  }
}

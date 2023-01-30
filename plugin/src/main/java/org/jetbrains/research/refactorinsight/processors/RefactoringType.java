package org.jetbrains.research.refactorinsight.processors;

import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.attributes.*;
import org.jetbrains.research.refactorinsight.data.classes.*;
import org.jetbrains.research.refactorinsight.data.methods.*;
import org.jetbrains.research.refactorinsight.data.packages.*;
import org.jetbrains.research.refactorinsight.data.variables.*;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.classes.*;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.methods.*;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.packages.*;

public enum RefactoringType {
    EXTRACT_OPERATION("Extract Method", new ExtractOperationJavaHandler(), new ExtractOperationKotlinHandler()),
    RENAME_CLASS("Rename Class", new RenameClassJavaHandler(), new RenameClassKotlinHandler()),
    MOVE_ATTRIBUTE("Move Attribute", new MoveAttributeJavaHandler(), null),
    MOVE_RENAME_ATTRIBUTE("Move And Rename Attribute", new MoveRenameAttributeJavaHandler(), null),
    REPLACE_ATTRIBUTE("Replace Attribute", new ReplaceAttributeJavaHandler(), null),
    RENAME_METHOD("Rename Method", new RenameMethodJavaHandler(), new RenameMethodKotlinHandler()),
    INLINE_OPERATION("Inline Method", new InlineOperationJavaHandler(), new InlineOperationKotlinHandler()),
    MOVE_OPERATION("Move Method", new MoveOperationJavaHandler(), new MoveOperationKotlinHandler()),
    MOVE_AND_RENAME_OPERATION("Move And Rename Method", new MoveOperationJavaHandler(), new MoveOperationKotlinHandler()),
    PULL_UP_OPERATION("Pull Up Method", new PullUpOperationJavaHandler(), new PullUpOperationKotlinHandler()),
    MOVE_CLASS("Move Class", new MoveClassJavaHandler(), new MoveClassKotlinHandler()),
    MOVE_RENAME_CLASS("Move And Rename Class", new MoveRenameClassJavaHandler(), new MoveRenameClassKotlinHandler()),
    MOVE_SOURCE_FOLDER("Move Source Folder", new MoveSourceFolderJavaHandler(), new MoveSourceFolderKotlinHandler()),
    PULL_UP_ATTRIBUTE("Pull Up Attribute", new PullUpAttributeJavaHandler(), null),
    PUSH_DOWN_ATTRIBUTE("Push Down Attribute", new PushDownAttributeJavaHandler(), null),
    PUSH_DOWN_OPERATION("Push Down Method", new PushDownOperationJavaHandler(), new PushDownOperationKotlinHandler()),
    EXTRACT_INTERFACE("Extract Interface", new ExtractSuperClassJavaHandler(), new ExtractSuperClassKotlinHandler()),
    EXTRACT_SUPERCLASS("Extract Superclass", new ExtractSuperClassJavaHandler(), new ExtractSuperClassKotlinHandler()),
    EXTRACT_SUBCLASS("Extract Subclass", new ExtractClassJavaHandler(), new ExtractClassKotlinHandler()),
    EXTRACT_CLASS("Extract Class", new ExtractClassJavaHandler(), new ExtractClassKotlinHandler()),
    MERGE_OPERATION("Merge Method", null, null),
    EXTRACT_AND_MOVE_OPERATION("Extract And Move Method", new ExtractOperationJavaHandler(), new ExtractOperationKotlinHandler()),
    MOVE_AND_INLINE_OPERATION("Move And Inline Method", new InlineOperationJavaHandler(), new InlineOperationKotlinHandler()),
    RENAME_PACKAGE("Change Package", new RenamePackageJavaHandler(), new RenamePackageKotlinHandler()),
    EXTRACT_VARIABLE("Extract Variable", new ExtractVariableJavaHandler(), null),
    EXTRACT_ATTRIBUTE("Extract Attribute", new ExtractAttributeJavaHandler(), null),
    INLINE_VARIABLE("Inline Variable", new InlineVariableJavaHandler(), null),
    RENAME_VARIABLE("Rename Variable", new RenameVariableJavaHandler(), null),
    RENAME_PARAMETER("Rename Parameter", new RenameVariableJavaHandler(), null),
    RENAME_ATTRIBUTE("Rename Attribute", new RenameAttributeJavaHandler(), null),
    MERGE_VARIABLE("Merge Variable", new MergeVariableJavaHandler(), null),
    MERGE_PARAMETER("Merge Parameter", new MergeVariableJavaHandler(), null),
    MERGE_ATTRIBUTE("Merge Attribute", new MergeAttributeJavaHandler(), null),
    SPLIT_VARIABLE("Split Variable", new SplitVariableJavaHandler(), null),
    SPLIT_PARAMETER("Split Parameter", new SplitVariableJavaHandler(), null),
    SPLIT_ATTRIBUTE("Split Attribute", new SplitAttributeJavaHandler(), null),
    REPLACE_VARIABLE_WITH_ATTRIBUTE("Replace Variable With Attribute", new RenameVariableJavaHandler(), null),
    PARAMETERIZE_VARIABLE("Parameterize Variable", new RenameVariableJavaHandler(), null),
    CHANGE_RETURN_TYPE("Change Return Type", new ChangeReturnTypeJavaHandler(), null),
    CHANGE_VARIABLE_TYPE("Change Variable Type", new ChangeVariableTypeJavaHandler(), null),
    CHANGE_PARAMETER_TYPE("Change Parameter Type", new ChangeVariableTypeJavaHandler(), null),
    CHANGE_ATTRIBUTE_TYPE("Change Attribute Type", new ChangeAttributeTypeJavaHandler(), null),
    ADD_METHOD_ANNOTATION("Add Method Annotation", new AddMethodAnnotationJavaHandler(), null),
    REMOVE_METHOD_ANNOTATION("Remove Method Annotation", new RemoveMethodAnnotationJavaHandler(), null),
    MODIFY_METHOD_ANNOTATION("Modify Method Annotation", new ModifyMethodAnnotationJavaHandler(), null),
    ADD_ATTRIBUTE_ANNOTATION("Add Attribute Annotation", new AddAttributeAnnotationJavaHandler(), null),
    REMOVE_ATTRIBUTE_ANNOTATION("Remove Attribute Annotation", new RemoveAttributeAnnotationJavaHandler(), null),
    MODIFY_ATTRIBUTE_ANNOTATION("Modify Attribute Annotation", new ModifyAttributeAnnotationJavaHandler(), null),
    ADD_CLASS_ANNOTATION("Add Class Annotation", new AddClassAnnotationJavaHandler(), null),
    REMOVE_CLASS_ANNOTATION("Remove Class Annotation", new RemoveClassAnnotationJavaHandler(), null),
    MODIFY_CLASS_ANNOTATION("Modify Class Annotation", new ModifyClassAnnotationJavaHandler(), null),
    ADD_PARAMETER_ANNOTATION("Add Parameter Annotation", new AddParameterAnnotationJavaHandler(), null),
    REMOVE_PARAMETER_ANNOTATION("Remove Parameter Annotation", new RemoveParameterAnnotationJavaHandler(), null),
    MODIFY_PARAMETER_ANNOTATION("Modify Parameter Annotation", new ModifyParameterAnnotationJavaHandler(), null),
    ADD_PARAMETER("Add Parameter", new AddParameterJavaHandler(), new AddParameterKotlinHandler()),
    REMOVE_PARAMETER("Remove Parameter", new RemoveParameterJavaHandler(), new RemoveParameterKotlinHandler()),
    REORDER_PARAMETER("Reorder Parameter", new ReorderParameterJavaHandler(), new ReorderParameterKotlinHandler()),
    RENAME_AND_CHANGE_ATTRIBUTE_TYPE("Rename and Change Attribute Type", null, null),
    RENAME_AND_CHANGE_PARAMETER_TYPE("Rename and Change Parameter Type", null, null),
    RENAME_AND_CHANGE_VARIABLE_TYPE("Rename and Change Variable Type", null, null);

    private final String name;
    private final JavaRefactoringHandler javaHandler;
    private final KotlinRefactoringHandler kotlinHandler;

    RefactoringType(String name, JavaRefactoringHandler javaHandler, KotlinRefactoringHandler kotlinHandler) {
        this.name = name;
        this.javaHandler = javaHandler;
        this.kotlinHandler = kotlinHandler;
    }

    public String getName() {
        return this.name;
    }

    public JavaRefactoringHandler getJavaHandler() {
        return this.javaHandler;
    }

    public KotlinRefactoringHandler getKotlinHandler() {
        return this.kotlinHandler;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

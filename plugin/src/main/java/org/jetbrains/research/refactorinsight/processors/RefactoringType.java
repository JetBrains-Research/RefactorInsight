package org.jetbrains.research.refactorinsight.processors;

import org.jetbrains.research.refactorinsight.data.JavaRefactoringHandler;
import org.jetbrains.research.refactorinsight.data.attributes.AddAttributeAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.AddAttributeModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.ChangeAttributeAccessModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.ChangeAttributeTypeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.EncapsulateAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.ExtractAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.InlineAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.MergeAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.ModifyAttributeAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.MoveAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.MoveRenameAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.PullUpAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.PushDownAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.RemoveAttributeAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.RemoveAttributeModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.RenameAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.ReplaceAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.attributes.SplitAttributeJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.AddClassAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.AddClassModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.ChangeClassAccessModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.ChangeTypeDeclarationKindJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.CollapseHierarchyJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.ExtractClassJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.ExtractSuperClassJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.MergeClassJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.ModifyClassAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.MoveClassJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.MoveRenameClassJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.RemoveClassAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.RemoveClassModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.RenameClassJavaHandler;
import org.jetbrains.research.refactorinsight.data.classes.SplitClassJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.AddMethodAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.AddMethodModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.AddParameterAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.AddParameterJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.AddThrownExceptionTypeJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ChangeMethodAccessModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ChangeReturnTypeJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ChangeThrownExceptionTypeJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ExtractOperationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.InlineOperationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.InvertConditionJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.MergeCatchJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.MergeConditionalJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ModifyMethodAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ModifyParameterAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.MoveOperationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.PullUpOperationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.PushDownOperationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.RemoveMethodAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.RemoveMethodModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.RemoveParameterAnnotationJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.RemoveParameterJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.RemoveThrownExceptionTypeJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.RenameMethodJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ReorderParameterJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ReplaceAnonymousWithLambdaJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ReplaceLoopWithPipelineJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.ReplacePipelineWithLoopJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.SplitConditionalJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.SplitMethodJavaHandler;
import org.jetbrains.research.refactorinsight.data.methods.MergeMethodJavaHandler;
import org.jetbrains.research.refactorinsight.data.packages.MergePackageJavaHandler;
import org.jetbrains.research.refactorinsight.data.packages.MoveSourceFolderJavaHandler;
import org.jetbrains.research.refactorinsight.data.packages.RenamePackageJavaHandler;
import org.jetbrains.research.refactorinsight.data.packages.SplitPackageJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.AddVariableModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.ChangeVariableTypeJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.ExtractVariableJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.InlineVariableJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.MergeVariableJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.RemoveVariableModifierJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.RenameVariableJavaHandler;
import org.jetbrains.research.refactorinsight.data.variables.SplitVariableJavaHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.KotlinRefactoringHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.classes.ExtractClassKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.classes.ExtractSuperClassKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.classes.MoveClassKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.classes.MoveRenameClassKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.classes.RenameClassKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.variables.AddParameterKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.methods.ExtractOperationKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.methods.InlineOperationKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.methods.MoveOperationKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.methods.PullUpOperationKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.methods.PushDownOperationKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.variables.RemoveParameterKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.methods.RenameMethodKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.variables.ReorderParameterKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.packages.MoveSourceFolderKotlinHandler;
import org.jetbrains.research.refactorinsight.kotlin.impl.data.packages.RenamePackageKotlinHandler;

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
    RENAME_AND_CHANGE_VARIABLE_TYPE("Rename and Change Variable Type", null, null),
    ADD_THROWN_EXCEPTION_TYPE("Add Thrown Exception Type", new AddThrownExceptionTypeJavaHandler(), null),
    REMOVE_THROWN_EXCEPTION_TYPE("Remove Thrown Exception Type", new RemoveThrownExceptionTypeJavaHandler(), null),
    CHANGE_THROWN_EXCEPTION_TYPE("Change Thrown Exception Type", new ChangeThrownExceptionTypeJavaHandler(), null),
    CHANGE_OPERATION_ACCESS_MODIFIER("Change Method Access Modifier", new ChangeMethodAccessModifierJavaHandler(), null),
    CHANGE_ATTRIBUTE_ACCESS_MODIFIER("Change Attribute Access Modifier", new ChangeAttributeAccessModifierJavaHandler(), null),
    ENCAPSULATE_ATTRIBUTE("Encapsulate Attribute", new EncapsulateAttributeJavaHandler(), null),
    PARAMETERIZE_ATTRIBUTE("Parameterize Attribute", new RenameVariableJavaHandler(), null),
    REPLACE_ATTRIBUTE_WITH_VARIABLE("Replace Attribute with Variable", new RenameVariableJavaHandler(), null),
    ADD_METHOD_MODIFIER("Add Method Modifier", new AddMethodModifierJavaHandler(), null),
    REMOVE_METHOD_MODIFIER("Remove Method Modifier", new RemoveMethodModifierJavaHandler(), null),
    ADD_ATTRIBUTE_MODIFIER("Add Attribute Modifier", new AddAttributeModifierJavaHandler(), null),
    REMOVE_ATTRIBUTE_MODIFIER("Remove Attribute Modifier", new RemoveAttributeModifierJavaHandler(), null),
    ADD_VARIABLE_MODIFIER("Add Variable Modifier", new AddVariableModifierJavaHandler(), null),
    REMOVE_VARIABLE_MODIFIER("Remove Variable Modifier", new RemoveVariableModifierJavaHandler(), null),
    ADD_PARAMETER_MODIFIER("Add Parameter Modifier", new AddVariableModifierJavaHandler(), null),
    REMOVE_PARAMETER_MODIFIER("Remove Parameter Modifier", new RemoveVariableModifierJavaHandler(), null),
    ADD_CLASS_MODIFIER("Add Class Modifier", new AddClassModifierJavaHandler(), null),
    REMOVE_CLASS_MODIFIER("Remove Class Modifier", new RemoveClassModifierJavaHandler(), null),
    CHANGE_CLASS_ACCESS_MODIFIER("Change Class Access Modifier", new ChangeClassAccessModifierJavaHandler(), null),
    MOVE_PACKAGE("Move Package", new RenamePackageJavaHandler(), null),
    SPLIT_PACKAGE("Split Package", new SplitPackageJavaHandler(), null),
    MERGE_PACKAGE("Merge Package", new MergePackageJavaHandler(), null),
    LOCALIZE_PARAMETER("Localize Parameter", new RenameVariableJavaHandler(), null),
    CHANGE_TYPE_DECLARATION_KIND("Change Type Declaration Kind", new ChangeTypeDeclarationKindJavaHandler(), null),
    COLLAPSE_HIERARCHY("Collapse Hierarchy", new CollapseHierarchyJavaHandler(), null),
    REPLACE_LOOP_WITH_PIPELINE("Replace Loop with Pipeline", new ReplaceLoopWithPipelineJavaHandler(), null),
    REPLACE_ANONYMOUS_WITH_LAMBDA("Replace Anonymous with Lambda", new ReplaceAnonymousWithLambdaJavaHandler(), null),
    MERGE_CLASS("Merge Class", new MergeClassJavaHandler(), null),
    INLINE_ATTRIBUTE("Inline Attribute", new InlineAttributeJavaHandler(), null),
    REPLACE_PIPELINE_WITH_LOOP("Replace Pipeline with Loop", new ReplacePipelineWithLoopJavaHandler(), null),
    SPLIT_CLASS("Split Class", new SplitClassJavaHandler(), null),
    SPLIT_CONDITIONAL("Split Conditional", new SplitConditionalJavaHandler(), null),
    INVERT_CONDITIONAL("Invert Condition", new InvertConditionJavaHandler(), null),
    MERGE_CONDITIONAL("Merge Conditional", new MergeConditionalJavaHandler(), null),
    MERGE_CATCH("Merge Catch", new MergeCatchJavaHandler(), null),
    MERGE_METHOD("Merge Method", new MergeMethodJavaHandler(), null),
    SPLIT_METHOD("Split Method", new SplitMethodJavaHandler(), null);

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

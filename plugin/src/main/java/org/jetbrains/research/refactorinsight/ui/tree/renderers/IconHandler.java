package org.jetbrains.research.refactorinsight.ui.tree.renderers;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import org.jetbrains.kotlin.idea.KotlinIcons;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;

import static org.jetbrains.research.refactorinsight.utils.TextUtils.isKotlinFile;

public abstract class IconHandler {
    protected abstract Icon specifyIcon(RefactoringInfo info, Node node);

    public Icon getIconFor(Group group, String filePath) {
        return switch (group) {
            case METHOD -> isKotlinFile(filePath) ? KotlinIcons.FUNCTION : AllIcons.Nodes.Method;
            case CLASS -> isKotlinFile(filePath) ? KotlinIcons.CLASS : AllIcons.Nodes.Class;
            case ATTRIBUTE -> isKotlinFile(filePath) ? KotlinIcons.FIELD_VAR : AllIcons.Nodes.Field;
            case VARIABLE -> isKotlinFile(filePath) ? KotlinIcons.FIELD_VAR : AllIcons.Nodes.Variable;
            case PARAMETER -> isKotlinFile(filePath) ? KotlinIcons.PARAMETER : AllIcons.Nodes.Parameter;
            case INTERFACE -> isKotlinFile(filePath) ? KotlinIcons.INTERFACE : AllIcons.Nodes.Interface;
            case ABSTRACT -> isKotlinFile(filePath) ? KotlinIcons.ABSTRACT_CLASS : AllIcons.Nodes.AbstractClass;
            case ANNOTATION -> isKotlinFile(filePath) ? KotlinIcons.ANNOTATION : AllIcons.Nodes.Annotationtype;
            case PACKAGE -> AllIcons.Nodes.Package;
        };
    }
}

package org.jetbrains.research.refactorinsight.ui.tree.renderers;

import java.util.EnumMap;
import java.util.Map;
import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import icons.RefactorInsightIcons;
import org.jetbrains.kotlin.idea.KotlinIcons;
import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.DisplayedGroup;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.AbstractClassHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.AttributeHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.ClassHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.InterfaceHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.MethodHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.PackageHandler;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers.VariableHandler;

import static org.jetbrains.research.refactorinsight.utils.TextUtils.isKotlinFile;

public class CellIconFactory {
    private final Map<Group, IconHandler> map = new EnumMap<>(Group.class);

    public CellIconFactory() {
        map.put(Group.ATTRIBUTE, new AttributeHandler());
        map.put(Group.ABSTRACT, new AbstractClassHandler());
        map.put(Group.INTERFACE, new InterfaceHandler());
        map.put(Group.METHOD, new MethodHandler());
        map.put(Group.CLASS, new ClassHandler());
        map.put(Group.PACKAGE, new PackageHandler());
        map.put(Group.VARIABLE, new VariableHandler());
    }

    /**
     * Get icon from a node type and info.
     */
    public Icon create(RefactoringInfo info, Node node) {
        return switch (node.getType()) {
            case GROUP -> groupIcon(info);
            case TYPE -> RefactorInsightIcons.node;
            default -> map.get(info.getGroup()).specifyIcon(info, node);
        };
    }

    private Icon groupIcon(RefactoringInfo info) {
        return switch (DisplayedGroup.fromInternalGroup(info.getGroup())) {
            case METHOD -> isKotlinFile(info.getLeftPath()) ? KotlinIcons.FUNCTION : AllIcons.Nodes.Method;
            case CLASS -> isKotlinFile(info.getLeftPath()) ? KotlinIcons.CLASS : AllIcons.Nodes.Class;
            case VARIABLE -> isKotlinFile(info.getLeftPath()) ? KotlinIcons.FIELD_VAR : AllIcons.Nodes.Variable;
            case ANNOTATION ->
                    isKotlinFile(info.getLeftPath()) ? KotlinIcons.ANNOTATION : AllIcons.Nodes.Annotationtype;
            case PARAMETER -> isKotlinFile(info.getLeftPath()) ? KotlinIcons.PARAMETER : AllIcons.Nodes.Parameter;
            case PACKAGE -> AllIcons.Nodes.Package;
        };
    }
}

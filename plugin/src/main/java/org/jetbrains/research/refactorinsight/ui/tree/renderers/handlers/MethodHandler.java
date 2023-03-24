package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;

import javax.swing.Icon;

import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.NodeType;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

public class MethodHandler extends IconHandler {
    @Override
    public Icon specifyIcon(RefactoringInfo info, Node node) {
        if (node.getType() == NodeType.DETAILS) {
            return getIconFor(Group.CLASS, info.getLeftPath());
        }

        if (node.getType() == NodeType.ELEMENTS) {
            if (info.getName().contains("Annotation")) {
                return getIconFor(Group.ANNOTATION, info.getLeftPath());
            }
            if (info.getName().contains("Parameter")) {
                return getIconFor(Group.PARAMETER, info.getLeftPath());
            }
            if (info.getName().contains("Return")) {
                return AllIcons.Nodes.Type;
            }
        }
        return getIconFor(Group.METHOD, info.getLeftPath());
    }
}
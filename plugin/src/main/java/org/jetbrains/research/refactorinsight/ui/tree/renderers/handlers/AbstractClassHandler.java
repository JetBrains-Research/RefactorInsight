package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import javax.swing.Icon;

import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.NodeType;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

public class AbstractClassHandler extends IconHandler {
    @Override
    public Icon specifyIcon(RefactoringInfo info, Node node) {
        if (node.getType() == NodeType.DETAILS) {
            return getIconFor(Group.PACKAGE, info.getLeftPath());
        }
        if (node.getType() == NodeType.ELEMENTS && info.getName().contains("Annotation")) {
            return getIconFor(Group.ANNOTATION, info.getLeftPath());
        }
        return getIconFor(Group.ABSTRACT, info.getLeftPath());
    }
}

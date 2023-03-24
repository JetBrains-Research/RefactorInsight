package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import javax.swing.Icon;

import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.NodeType;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

public class VariableHandler extends IconHandler {
    @Override
    public Icon specifyIcon(RefactoringInfo info, Node node) {
        if (node.getType() == NodeType.NAME) {
            return getIconFor(Group.METHOD, info.getLeftPath());
        }
        return getIconFor(Group.VARIABLE, info.getLeftPath());
    }
}

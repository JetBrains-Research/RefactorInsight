package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import org.jetbrains.research.refactorinsight.data.Group;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

import javax.swing.*;

public class PackageHandler extends IconHandler {
    @Override
    public Icon specifyIcon(RefactoringInfo info, Node node) {
        return getIconFor(Group.PACKAGE, info.getLeftPath());
    }
}
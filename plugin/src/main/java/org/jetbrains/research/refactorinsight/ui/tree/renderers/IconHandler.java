package org.jetbrains.research.refactorinsight.ui.tree.renderers;

import javax.swing.Icon;

import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;

public interface IconHandler {

    Icon getIcon(RefactoringInfo info, Node node);

}

package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import javax.swing.Icon;

import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.NodeType;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

public class VariableHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, Node node) {
    if (node.getType() == NodeType.NAME) {
      return AllIcons.Nodes.Method;
    }
    return AllIcons.Nodes.Variable;
  }
}

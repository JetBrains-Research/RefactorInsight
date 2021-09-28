package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import javax.swing.Icon;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.NodeType;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

public class MethodHandler implements IconHandler {

  @Override
  public Icon getIcon(RefactoringInfo info, Node node) {
    if (node.getType() == NodeType.DETAILS) {
      return AllIcons.Nodes.Class;
    }
    if (node.getType() == NodeType.ELEMENTS) {
      if (info.getName().contains("Annotation")) {
        return AllIcons.Nodes.Annotationtype;
      }
      if (info.getName().contains("Parameter")) {
        return AllIcons.Nodes.Parameter;
      }
      if (info.getName().contains("Return")) {
        return AllIcons.Nodes.Type;
      }
    }
    return AllIcons.Nodes.Method;
  }
}
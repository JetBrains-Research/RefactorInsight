package org.jetbrains.research.refactorinsight.ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import javax.swing.Icon;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.NodeType;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.IconHandler;

public class InterfaceHandler implements IconHandler {

  @Override
  public Icon getIcon(RefactoringInfo info, Node node) {
    if (node.getType() == NodeType.DETAILS) {
      return AllIcons.Nodes.Package;
    }
    if (node.getType() == NodeType.ELEMENTS && info.getName().contains("Annotation")) {
      return AllIcons.Nodes.Annotationtype;
    }
    return AllIcons.Nodes.Interface;
  }
}
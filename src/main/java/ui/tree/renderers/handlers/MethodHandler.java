package ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.Node;
import ui.tree.NodeType;
import ui.tree.renderers.IconHandler;

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
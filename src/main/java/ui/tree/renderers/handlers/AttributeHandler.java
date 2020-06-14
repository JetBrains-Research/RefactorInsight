package ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.Node;
import ui.tree.NodeType;
import ui.tree.renderers.IconHandler;

public class AttributeHandler implements IconHandler {

  @Override
  public Icon getIcon(RefactoringInfo info, Node node) {
    if (node.getType() == NodeType.DETAILS) {
      return AllIcons.Nodes.Class;
    }
    if (node.getType() == NodeType.ELEMENTS) {
      return AllIcons.Nodes.Annotationtype;
    }

    return AllIcons.Nodes.Field;
  }
}
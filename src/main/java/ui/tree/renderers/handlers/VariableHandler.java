package ui.tree.renderers.handlers;

import com.intellij.icons.AllIcons;
import data.RefactoringInfo;
import javax.swing.Icon;
import ui.tree.Node;
import ui.tree.NodeType;
import ui.tree.renderers.IconHandler;

public class VariableHandler implements IconHandler {
  @Override
  public Icon getIcon(RefactoringInfo info, Node node) {
    if (node.getType() == NodeType.NAME) {
      return AllIcons.Nodes.Method;
    }
    return AllIcons.Nodes.Variable;
  }
}

package data;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBDefaultTreeCellRenderer;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

class MyCellRenderer extends JBDefaultTreeCellRenderer {

  public MyCellRenderer() {
    super();
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                boolean expanded, boolean leaf, int row,
                                                boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    RefactoringInfo info = (RefactoringInfo) node.getUserObjectPath()[1];

    String name = info.getType().getDisplayName();
    if (node.getUserObject() instanceof RefactoringInfo) {
      setText(name);
    }

    Icon icon = null;
    if (leaf) {
      icon = AllIcons.Actions.Diff;
    } else if (name.contains("Class")) {
      icon = AllIcons.Nodes.Class;
    } else if (name.contains("Method")) {
      icon = AllIcons.Nodes.Method;
    } else if (name.contains("Attribute")) {
      icon = AllIcons.Nodes.ObjectTypeAttribute;
    } else if (name.contains("Variable")) {
      icon = AllIcons.Nodes.Variable;
    } else if (name.contains("Parameter")) {
      icon = AllIcons.Nodes.Parameter;
    } else if (name.contains("Package")) {
      icon = AllIcons.Nodes.Package;
    } else if (name.contains("Return")) {
      icon = AllIcons.Debugger.WatchLastReturnValue;
    } else if (name.contains("Interface")) {
      icon = AllIcons.Nodes.Interface;
    }
    setIcon(icon);

    return this;
  }
}

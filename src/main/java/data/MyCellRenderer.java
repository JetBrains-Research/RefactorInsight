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

    if (node.getUserObject() instanceof RefactoringInfo) {
      RefactoringInfo ri = (RefactoringInfo) node.getUserObject();
      setText(ri.getType().getDisplayName());
      if (node.toString().contains("Class")) {
        Icon icon = AllIcons.Nodes.Class;
        setIcon(icon);
      } else if (node.toString().contains("Operation") || node.toString().contains("Method")) {
        Icon icon = AllIcons.Nodes.Method;
        setIcon(icon);
      } else if (node.toString().contains("Attribute")) {
        Icon icon = AllIcons.Nodes.ObjectTypeAttribute;
        setIcon(icon);
      } else if (node.toString().contains("Variable")) {
        Icon icon = AllIcons.Nodes.Variable;
        setIcon(icon);
      } else if (node.toString().contains("Parameter")) {
        Icon icon = AllIcons.Nodes.Parameter;
        setIcon(icon);
      } else if (node.toString().contains("Package")) {
        Icon icon = AllIcons.Nodes.Package;
        setIcon(icon);
      } else if (node.toString().contains("Return")) {
        Icon icon = AllIcons.Debugger.WatchLastReturnValue;
        setIcon(icon);
      } else if (node.toString().contains("Interface")) {
        Icon icon = AllIcons.Nodes.Interface;
        setIcon(icon);
      }
    } else if (leaf) {
      setIcon(AllIcons.Actions.Diff);
    } else {
      setIcon(null);
    }
    return this;
  }
}

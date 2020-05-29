package data;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBDefaultTreeCellRenderer;
import com.intellij.util.text.JBDateFormat;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyCellRenderer extends JBDefaultTreeCellRenderer {

  private boolean isMethodHistory = false;

  public MyCellRenderer() {
    super();
  }

  public MyCellRenderer(boolean isMethodHistory) {
    super();
    this.isMethodHistory = isMethodHistory;
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                boolean expanded, boolean leaf, int row,
                                                boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.equals(node.getRoot())) {
      return this;
    }
    RefactoringInfo info = (RefactoringInfo) node.getUserObjectPath()[1];

    String name = info.getType().getDisplayName();
    if (node.getUserObject() instanceof RefactoringInfo) {
      if (isMethodHistory) {
        setText(name + " at " + JBDateFormat.getFormatter()
            .formatPrettyDateTime(info.getTimestamp()));
      } else {
        setText(name);
      }
    }

    Icon icon = null;
    if (leaf && !isMethodHistory) {
      icon = AllIcons.Actions.Diff;
    } else if (name.contains("Class")) {
      icon = AllIcons.Nodes.Class;
    } else if (name.contains("Method")) {
      icon = AllIcons.Nodes.Method;
    } else if (name.contains("Attribute")) {
      icon = AllIcons.Nodes.Field;
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
    if (node.getRoot().equals(node.getParent())) {
      icon = AllIcons.Actions.SuggestedRefactoringBulb;
    }
    if (leaf && isMethodHistory) {
      icon = null;
    }
    setIcon(icon);
    return this;
  }
}

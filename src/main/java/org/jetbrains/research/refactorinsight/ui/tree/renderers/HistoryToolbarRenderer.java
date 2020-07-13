package org.jetbrains.research.refactorinsight.ui.tree.renderers;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.text.JBDateFormat;
import icons.RefactorInsightIcons;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;

public class HistoryToolbarRenderer extends ColoredTreeCellRenderer {

  private CellIconFactory factory = new CellIconFactory();

  /**
   * Returns the Refactoring Info parent of a node.
   * This may be at position 1 or 3.
   *
   * @param node current position in the tree.
   * @return the refactoring info parent.
   */
  public static RefactoringInfo getRefactoringInfo(DefaultMutableTreeNode node) {
    RefactoringInfo info = null;
    if (node.getUserObjectPath()[1] instanceof RefactoringInfo) {
      info = (RefactoringInfo) node.getUserObjectPath()[1];
    } else {
      if (node.getUserObjectPath().length > 3
          && node.getUserObjectPath()[3] instanceof RefactoringInfo) {
        info = (RefactoringInfo) node.getUserObjectPath()[3];
      }
    }
    return info;
  }

  @Override
  public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                    boolean expanded, boolean leaf, int row, boolean hasFocus) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.equals(node.getRoot())) {
      return;
    }

    Icon icon = null;
    RefactoringInfo info = getRefactoringInfo(node);

    if (node.getUserObject() instanceof Node) {
      final Node object = (Node) node.getUserObject();
      icon = factory.create(info, object);
      append(object.getContent());
    } else if (node.getUserObject() instanceof RefactoringInfo) {
      append(info.getName());
      icon = RefactorInsightIcons.node;
    } else if (node.getParent().equals(node.getRoot())) {
      append(node.toString(), SimpleTextAttributes.GRAY_ATTRIBUTES);
    } else {
      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
      if (parent.getUserObject().equals("Check methods in this class")) {
        icon = AllIcons.Nodes.Method;
      } else {
        icon = AllIcons.Nodes.Field;
      }
      append(node.toString());
    }
    if (leaf) {
      append(" " + JBDateFormat.getFormatter()
          .formatPrettyDateTime(info.getTimestamp()), SimpleTextAttributes.GRAY_ATTRIBUTES);

    }
    setIcon(icon);
  }
}

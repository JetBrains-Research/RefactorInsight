package ui.renderer;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBDefaultTreeCellRenderer;
import com.intellij.util.text.JBDateFormat;
import data.RefactoringInfo;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;

public class CellRenderer extends JBDefaultTreeCellRenderer {

  private boolean isMethodHistory = false;
  private CellIconFactory factory = new CellIconFactory();

  public CellRenderer() {
    super();
  }

  public CellRenderer(boolean isMethodHistory) {
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
    Icon icon = factory.create(info);

    if (node.getUserObject() instanceof RefactoringInfo) {
      setText(isMethodHistory ? getText(info) : info.getName());
      icon = AllIcons.Actions.SuggestedRefactoringBulb;
    }

    if (leaf) {
      icon = isMethodHistory ? null : AllIcons.Actions.Diff;
    }

    setIcon(icon);
    return this;
  }

  @NotNull
  private String getText(RefactoringInfo info) {
    StringBuffer html = new StringBuffer(
        "<html> " + info.getName() + " <font color=\"#696969\"> "
            + JBDateFormat.getFormatter()
            .formatPrettyDateTime(info.getTimestamp()) + "</font></html>");
    return html.toString();
  }
}

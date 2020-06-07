package ui.renderer;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBDefaultTreeCellRenderer;
import data.RefactoringInfo;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import utils.Utils;

public class CellRenderer extends JBDefaultTreeCellRenderer {

  private CellIconFactory factory = new CellIconFactory();
  private boolean methodHistory = false;

  public CellRenderer() {
    super();
  }

  public CellRenderer(boolean methodHistory) {
    super();
    this.methodHistory = methodHistory;
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

    Icon icon = factory.create(info, leaf, node.getUserObjectPath().length > 2
        && node.getUserObject().equals(node.getUserObjectPath()[2]));


    if (node.getUserObject() instanceof RefactoringInfo) {
      setText(isMethodHistory() ? Utils.getTextMethodToolbar(info)
          : Utils.getTextLogUI(info));
      icon = AllIcons.Actions.SuggestedRefactoringBulb;
    }

    if (leaf) {
      setText(Utils.createHtml(
          Utils.getStringLeaf(info.getLineMarkings().size() > 0
                  ? String.valueOf(info.getLineMarkings().get(0).getRightStart() + 1)
                  : "", getText(),
              info.getRightPath() != null ? info.getRightPath() : "")));
    }

    setIcon(icon);
    return this;
  }

  public boolean isMethodHistory() {
    return methodHistory;
  }
}

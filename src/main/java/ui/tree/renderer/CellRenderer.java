package ui.tree.renderer;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.text.JBDateFormat;
import data.RefactoringInfo;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;

public class CellRenderer extends ColoredTreeCellRenderer {

  private final CellIconFactory factory = new CellIconFactory();
  private boolean methodHistory = false;

  public CellRenderer() {
    super();
  }

  public CellRenderer(boolean methodHistory) {
    super();
    this.methodHistory = methodHistory;
  }


  @Override
  public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                    boolean expanded, boolean leaf, int row, boolean hasFocus) {

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.equals(node.getRoot())) {
      return;
    }

    RefactoringInfo info = (RefactoringInfo) node.getUserObjectPath()[1];

    Icon icon = factory.create(info, leaf, node.getUserObjectPath().length > 2
        && node.getUserObject().equals(node.getUserObjectPath()[2]));


    if (node.getUserObject() instanceof RefactoringInfo) {
      if (isMethodHistory()) {
        append(info.getName() + " ");
        append(JBDateFormat.getFormatter()
            .formatPrettyDateTime(info.getTimestamp()), SimpleTextAttributes.GRAY_ATTRIBUTES);
      } else {
        append(info.getName(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        final int size = info.getIncludingRefactorings().size();
        if (size > 0) {
          append(" implied " + info.getIncludingRefactorings()
                  .toString().replace("[", "").replace("]", ""),
              SimpleTextAttributes.GRAY_ATTRIBUTES);
        }
      }
      icon = AllIcons.Actions.SuggestedRefactoringBulb;
    } else if (leaf && !methodHistory) {
      append((info.getLineMarkings().size() > 0
              ? ((info.getLineMarkings().get(0).getRightStart() + 1) + " ") : ""),
          SimpleTextAttributes.GRAY_ATTRIBUTES);
      append(node.toString() + " ");
      append((info.getRightPath() != null ? ("in file " + info.getRightPath()) : ""),
          SimpleTextAttributes.GRAY_ATTRIBUTES);
    } else {
      append(node.toString());
    }

    setIcon(icon);
  }

  public boolean isMethodHistory() {
    return methodHistory;
  }
}

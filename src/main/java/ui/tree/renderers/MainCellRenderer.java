package ui.tree.renderers;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import data.RefactoringInfo;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;
import ui.tree.Node;

public class MainCellRenderer extends ColoredTreeCellRenderer {

  private final CellIconFactory factory = new CellIconFactory();

  public MainCellRenderer() {
    super();
  }

  @Override
  public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                    boolean expanded, boolean leaf, int row, boolean hasFocus) {

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    if (node.equals(node.getRoot())) {
      return;
    }

    RefactoringInfo info = (RefactoringInfo) node.getUserObjectPath()[1];
    Icon icon = null;
    Node object = null;

    if (node.getUserObject() instanceof Node) {
      object = (Node) node.getUserObject();
      icon = factory.create(info, object);
    }
    if (node.getUserObject() instanceof RefactoringInfo) {
      append(info.getName(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
      int size = info.getIncludingRefactorings().size();
      if (size > 0) {
        append(" implied " + info.getIncludingRefactorings()
                .toString().replace("[", "").replace("]", ""),
            SimpleTextAttributes.GRAY_ATTRIBUTES);
      }
      icon = AllIcons.Actions.SuggestedRefactoringBulb;
    } else if (leaf) {
      append((info.getLineMarkings().size() > 0
              ? ((info.getLineMarkings().get(0).getRightStart() + 1) + " ") : ""),
          SimpleTextAttributes.GRAY_ATTRIBUTES);
      append(object.getContent() + " ");
      append((info.getRightPath() != null ? ("in file " + info.getRightPath()) : ""),
          SimpleTextAttributes.GRAY_ATTRIBUTES);
    } else {
      append(object.getContent());
    }
    setIcon(icon);
  }

}

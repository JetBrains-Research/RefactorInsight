package org.jetbrains.research.refactorinsight.ui.tree.renderers;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import icons.RefactorInsightIcons;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.NodeType;

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

    Node object = (Node) node.getUserObject();
    RefactoringInfo info = object.getInfo();
    Icon icon = factory.create(info, object);

    if (object.getType() == NodeType.TYPE) {
      append(info.getName(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
      int size = info.getIncludingRefactorings().size();
      if (size > 0) {
        append(" implied " + info.getIncludingRefactorings()
                .toString().replace("[", "").replace("]", ""),
            SimpleTextAttributes.GRAY_ATTRIBUTES);
      }
      icon = RefactorInsightIcons.node;
    } else if (leaf) {
      append((info.getLineMarkings().size() > 0
              ? ((info.getLineMarkings().get(0).getRightStart() + 1) + " ") : ""),
          SimpleTextAttributes.GRAY_ATTRIBUTES);
      append(object.getContent() + " ");
      append(((info.getRightPath() != null && !info.getRightPath().isEmpty())
          ? ("in file " + info.getRightPath()) : ""), SimpleTextAttributes.GRAY_ATTRIBUTES);
    } else {
      append(object.getContent());
    }
    setIcon(icon);
  }

}

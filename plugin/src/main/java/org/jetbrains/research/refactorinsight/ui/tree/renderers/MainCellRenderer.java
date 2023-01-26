package org.jetbrains.research.refactorinsight.ui.tree.renderers;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.ui.tree.DisplayedGroup;
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
    if (node.isRoot()) {
      return;
    }

    Node object = (Node) node.getUserObject();
    RefactoringInfo info = object.getInfo();
    setIcon(factory.create(info, object));

    if (object.getType() == NodeType.GROUP) {
      String groupName = DisplayedGroup.fromInternalGroup(info.getGroup()).toString();
      String capitalizedName = groupName.substring(0, 1).toUpperCase() + groupName.substring(1).toLowerCase();
      append(capitalizedName, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
    } else if (object.getType() == NodeType.TYPE) {
      append(info.getName(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
      int size = info.getIncludingRefactorings().size();
      if (size > 0) {
        append(" implied " + info.getIncludingRefactorings()
                .toString().replace("[", "").replace("]", ""),
            SimpleTextAttributes.GRAY_ATTRIBUTES);
      }
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
  }

}

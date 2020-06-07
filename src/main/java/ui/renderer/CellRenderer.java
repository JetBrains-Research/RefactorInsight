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

    Icon icon = factory.create(info, leaf, node.getUserObjectPath().length > 2
        && node.getUserObject().equals(node.getUserObjectPath()[2]));


    if (node.getUserObject() instanceof RefactoringInfo) {
      setText(isMethodHistory ? getTextMethodToolbar(info) : getTextLogUI(info));
      icon = AllIcons.Actions.SuggestedRefactoringBulb;
    }

    if (leaf) {
      setText(createHtml(
          getStringLeaf(info.getLineMarkings().size() > 0
                  ? String.valueOf(info.getLineMarkings().get(0).getRightStart() + 1)
                  : "", getText(),
              info.getRightPath() != null ? info.getRightPath() : "")));
    }

    setIcon(icon);
    return this;
  }

  @NotNull
  private String getTextMethodToolbar(RefactoringInfo info) {
    final String second = JBDateFormat.getFormatter()
        .formatPrettyDateTime(info.getTimestamp());
    final String first = info.getName();
    return createHtml(second, first);
  }

  @NotNull
  private String getTextLogUI(RefactoringInfo info) {
    String second =
        info.getIncludingRefactorings().size() > 0 ? info.getIncludingRefactorings().toString() :
            "  ";
    second = second.substring(1, second.length() - 1);
    second = second.equals("") ? "" : (" implied " + second);
    final String first = info.getName();
    return createHtml(second, first);
  }

  @NotNull
  private String createHtml(String second, String first) {
    final String str = getStringForRefactoringNode(first, second);
    return createHtml(str);
  }

  @NotNull
  private String createHtml(String str) {
    StringBuffer html = new StringBuffer(str);
    return html.toString();
  }

  @NotNull
  private String getStringForRefactoringNode(String first, String second) {
    return "<html> <b>" + first + "</b> <font color=\"#696969\"> "
        + second + "</font></html>";
  }

  private String getStringLeaf(String first, String second, String third) {
    return "<html> <font color=\"#696969\"> " + first + " </font> "
        + second + (third.equals("") ? "</html>"
        : ("<font color=\"#696969\"> in file " + third + " </font></html>"));
  }
}

package ui.tree;

import com.intellij.ui.treeStructure.Tree;
import data.RefactoringInfo;
import data.RefactoringInfo.Group;
import javax.swing.tree.DefaultMutableTreeNode;
import utils.StringUtils;

public class TreeUtils {
  /**
   * Method for create a presentable String out of the
   * element changes for a refactoring.
   *
   * @return presentable String that shows the changes.
   */
  public static String getDisplayableElement(String elementBefore, String elementAfter) {
    if (elementBefore == null || elementBefore.isEmpty()) {
      return null;
    }
    String info = elementBefore;
    if (elementAfter != null && !elementAfter.isEmpty()) {
      info += " -> " + elementAfter;
    }
    return info;
  }

  /**
   * Method for create a presentable String out of the
   * element changes for a refactoring.
   *
   * @return presentable String that shows the changes.
   */
  public static String getDisplayableDetails(String before, String after) {
    if (before == null || after == null
        || before.isEmpty() || after.isEmpty()) {
      return null;
    }

    int index = StringUtils.indexOfDifference(before, after);

    String afterNew = after.substring(index);
    String beforeNew = before.substring(index);

    if (beforeNew.equals(afterNew)) {
      return beforeNew;
    } else {
      return beforeNew + " -> " + afterNew;
    }
  }

  /**
   * Creates a DefaultMutableTreeNode for the UI.
   *
   * @return the node.
   */
  public static DefaultMutableTreeNode makeNode(RefactoringInfo info) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
    DefaultMutableTreeNode root = makeDetailsNode(node, info);
    makeNameNode(root, info);
    return node;
  }

  /**
   * Creates a node based on the nameBefore & nameAfter attributes.
   *
   * @param root to add node to.
   */
  public static void makeNameNode(DefaultMutableTreeNode root, RefactoringInfo refactoringInfo) {
    Group group = refactoringInfo.getGroup();
    DefaultMutableTreeNode child = new DefaultMutableTreeNode(
        (group == Group.METHOD || group == Group.CLASS || group == Group.ABSTRACT
            || group == Group.INTERFACE)
            ? StringUtils.getDisplayableName(refactoringInfo)
            : getDisplayableDetails(refactoringInfo.getNameBefore(),
            refactoringInfo.getNameAfter()));
    root.add(child);
    addLeaves(child, refactoringInfo);
  }

  /**
   * Creates a node iff the detailsBefore & detailsAfter attributes are not null.
   *
   * @param root current root of the tree.
   * @return the same root if no node was created.
   */
  private static DefaultMutableTreeNode makeDetailsNode(DefaultMutableTreeNode root,
                                                        RefactoringInfo info) {
    if (getDisplayableDetails(info.getDetailsBefore(), info.getDetailsAfter()) != null) {
      DefaultMutableTreeNode details =
          new DefaultMutableTreeNode(
              getDisplayableDetails(info.getDetailsBefore(), info.getDetailsAfter()));
      root.add(details);
      return details;
    }
    return root;
  }

  /**
   * Adds leaves for refactorings where the element
   * is not null.
   *
   * @param node to add leaves to.
   */
  public static void addLeaves(DefaultMutableTreeNode node, RefactoringInfo ref) {
    String displayableElement =
        getDisplayableElement(ref.getElementBefore(), ref.getElementAfter());
    if (displayableElement == null) {
      return;
    }
    node.add(new DefaultMutableTreeNode(displayableElement));
  }

  /**
   * Expands all the nodes of the tree.
   *
   * @param tree          to be expanded.
   * @param startingIndex the starting index for expansion.
   * @param rowCount      of the tree.
   */
  public static void expandAllNodes(Tree tree, int startingIndex, int rowCount) {
    for (int i = startingIndex; i < rowCount; ++i) {
      tree.expandRow(i);
    }
    if (tree.getRowCount() != rowCount) {
      expandAllNodes(tree, rowCount, tree.getRowCount());
    }
  }
}

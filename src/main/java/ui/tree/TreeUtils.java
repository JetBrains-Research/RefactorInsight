package ui.tree;

import com.intellij.ui.treeStructure.Tree;
import data.RefactoringInfo;
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
    if (elementBefore == null) {
      return null;
    }
    String info = elementBefore;
    if (elementAfter != null) {
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
    if (before == null || after == null || before.length() == 0 || after.length() == 0) {
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

    DefaultMutableTreeNode detailsNode = makeDetailsNode(info);
    DefaultMutableTreeNode nameNode = makeNameNode(info);
    DefaultMutableTreeNode leaf = makeLeafNode(info);
    if (leaf != null) {
      nameNode.add(leaf);
    }
    if (detailsNode != null) {
      detailsNode.add(nameNode);
      node.add(detailsNode);
    } else {
      node.add(nameNode);
    }
    return node;
  }

  /**
   * Creates a node based on the nameBefore & nameAfter attributes.
   */
  public static DefaultMutableTreeNode makeNameNode(RefactoringInfo refactoringInfo) {
    return new DefaultMutableTreeNode(
        new Node(NodeType.NAME, refactoringInfo.getDisplayableName()));
  }

  /**
   * Creates a node iff the detailsBefore & detailsAfter attributes are not null.
   *
   * @return the same root if no node was created.
   */
  public static DefaultMutableTreeNode makeDetailsNode(RefactoringInfo info) {
    final String displayableDetails =
        getDisplayableDetails(info.getDetailsBefore(), info.getDetailsAfter());
    if (displayableDetails != null && displayableDetails.length() > 0) {
      return new DefaultMutableTreeNode(new Node(NodeType.DETAILS, displayableDetails));
    } else {
      return null;
    }
  }

  /**
   * Adds leaves for refactorings where the element
   * is not null.
   */
  public static DefaultMutableTreeNode makeLeafNode(RefactoringInfo ref) {
    String displayableElement =
        getDisplayableElement(ref.getElementBefore(), ref.getElementAfter());
    if (displayableElement != null) {
      return new DefaultMutableTreeNode(new Node(NodeType.ELEMENTS, displayableElement));
    }
    return null;
  }

  /**
   * Expands all nodes of the tree.
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

  /**
   * Creates a presentable tree for the object's refactoring history.
   * It creates a node iff the information is relevant (if anything has changed).
   *
   * @param root to add the tree to.
   * @param ref  refactoring info.
   */
  public static void createHistoryTree(DefaultMutableTreeNode root, RefactoringInfo ref) {
    DefaultMutableTreeNode details = makeDetailsNode(ref);
    DefaultMutableTreeNode names = makeNameNode(ref);
    DefaultMutableTreeNode elements = makeLeafNode(ref);
    Node d = details != null ? (Node) details.getUserObject() : null;
    Node n = (Node) names.getUserObject();

    DefaultMutableTreeNode node = new DefaultMutableTreeNode(ref);

    if (details != null && d.getContent().contains(" -> ")) {
      node.add(details);
      if (n.getContent().contains(" -> ")) {
        details.add(names);
        if (elements != null) {
          names.add(elements);
        }
      } else {
        if (elements != null) {
          details.add(elements);
        }
      }
    } else if (n.getContent().contains(" -> ")) {
      node.add(names);
      if (elements != null) {
        names.add(elements);
      }
    } else if (elements != null) {
      node.add(elements);
    }
    root.add(node);
  }
}

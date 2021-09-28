package org.jetbrains.research.refactorinsight.ui.tree;

import org.jetbrains.research.refactorinsight.common.data.RefactoringInfo;

public class Node {

  private final NodeType type;
  private final String content;
  private final RefactoringInfo info;

  /**
   * Constructing the node of the rendering tree.
   * @param type Node type in tree
   * @param content Main content of node (optional)
   * @param info info of associated refactoring
   */
  public Node(NodeType type, String content, RefactoringInfo info) {
    this.type = type;
    this.content = content;
    this.info = info;
  }

  public NodeType getType() {
    return type;
  }

  public String getContent() {
    return content;
  }

  public RefactoringInfo getInfo() {
    return info;
  }
}

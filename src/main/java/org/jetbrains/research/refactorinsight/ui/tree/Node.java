package org.jetbrains.research.refactorinsight.ui.tree;

import org.jetbrains.research.refactorinsight.data.RefactoringInfo;

public class Node {

  private final NodeType type;
  private final String content;
  private final RefactoringInfo info;

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

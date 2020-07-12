package org.jetbrains.research.refactorinsight.ui.tree;

public class Node {

  private NodeType type;
  private String content;

  public Node(NodeType type, String content) {
    this.type = type;
    this.content = content;
  }

  public NodeType getType() {
    return type;
  }

  public String getContent() {
    return content;
  }
}

package org.jetbrains.research.refactorinsight.folding;

public final class Folding {
  public final String hintText;
  public final FoldingPositions positions;

  public Folding(String hintText, FoldingPositions positions) {
    this.hintText = hintText;
    this.positions = positions;
  }
}

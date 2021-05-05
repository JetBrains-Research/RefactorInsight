package org.jetbrains.research.refactorinsight.folding;

import gr.uom.java.xmi.UMLOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FoldingPositions {
  public final int hintOffset;
  public final int foldingStartOffset;
  public final int foldingEndOffset;

  public FoldingPositions(int hintOffset, int foldingStartOffset, int foldingEndOffset) {
    this.hintOffset = hintOffset;
    this.foldingStartOffset = foldingStartOffset;
    this.foldingEndOffset = foldingEndOffset;
  }

  @NotNull
  public static FoldingPositions fromMethod(@NotNull UMLOperation method) {
    int hintOffset = method.getLocationInfo().getStartOffset();
    int foldingStartOffset = method.getBody().getCompositeStatement().getLocationInfo().getStartOffset();
    int foldingEndOffset = method.getBody().getCompositeStatement().getLocationInfo().getEndOffset();
    return new FoldingPositions(
        hintOffset,
        foldingStartOffset,
        foldingEndOffset
    );
  }

  @NotNull
  public static FoldingPositions fromMethod(@NotNull org.jetbrains.research.kotlinrminer.uml.UMLOperation method) {
    int hintOffset = method.getLocationInfo().getStartOffset();
    int foldingStartOffset = method.getBody().getCompositeStatement().getLocationInfo().getStartOffset();
    int foldingEndOffset = method.getBody().getCompositeStatement().getLocationInfo().getEndOffset();
    return new FoldingPositions(
        hintOffset,
        foldingStartOffset,
        foldingEndOffset
    );
  }

  @NotNull
  public String toString() {
    return hintOffset + "/" + foldingStartOffset + "/" + foldingEndOffset;
  }

  @Nullable
  public static FoldingPositions fromString(@NotNull String value) {
    if (value.isEmpty()) {
      return null;
    }
    String[] values = value.split("/");
    return new FoldingPositions(
        Integer.parseInt(values[0]),
        Integer.parseInt(values[1]),
        Integer.parseInt(values[2])
    );
  }
}

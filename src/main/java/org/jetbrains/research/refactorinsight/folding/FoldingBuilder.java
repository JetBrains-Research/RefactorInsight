package org.jetbrains.research.refactorinsight.folding;

import gr.uom.java.xmi.UMLOperation;
import org.jetbrains.annotations.NotNull;

public final class FoldingBuilder {

  /**
   * Creates {@link FoldingDescriptor} instance for Kotlin method.
   */
  @NotNull
  public static FoldingDescriptor fromMethod(@NotNull UMLOperation method) {
    int hintOffset = method.getLocationInfo().getStartOffset();
    int foldingStartOffset = method.getBody().getCompositeStatement().getLocationInfo().getStartOffset();
    int foldingEndOffset = method.getBody().getCompositeStatement().getLocationInfo().getEndOffset();
    return new FoldingDescriptor(
        hintOffset,
        foldingStartOffset,
        foldingEndOffset
    );
  }

  /**
   * Creates {@link FoldingDescriptor} instance for Java method.
   */
  @NotNull
  public static FoldingDescriptor fromMethod(@NotNull org.jetbrains.research.kotlinrminer.uml.UMLOperation method) {
    int hintOffset = method.getLocationInfo().getStartOffset();
    int foldingStartOffset = method.getBody().getCompositeStatement().getLocationInfo().getStartOffset();
    int foldingEndOffset = method.getBody().getCompositeStatement().getLocationInfo().getEndOffset();
    return new FoldingDescriptor(
        hintOffset,
        foldingStartOffset,
        foldingEndOffset
    );
  }
}

package org.jetbrains.research.refactorinsight.data;

import gr.uom.java.xmi.UMLOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;

public class FoldingBuilder {
    /**
     * Creates {@link FoldingDescriptor} instance for Java method.
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
}

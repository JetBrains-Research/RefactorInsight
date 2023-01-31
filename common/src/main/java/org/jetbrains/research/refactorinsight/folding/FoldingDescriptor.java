package org.jetbrains.research.refactorinsight.folding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FoldingDescriptor {
    @Nullable
    private transient String hintText = null;
    private transient boolean isHintTextUnited = false;

    private final int hintOffset;
    private final int foldingStartOffset;
    private final int foldingEndOffset;

    /**
     * Creates a folding descriptor.
     *
     * @param hintOffset  offset of the hint,
     * @param startOffset start offset of the code block to fold,
     * @param endOffset   end offset of the code block to fold.
     */
    public FoldingDescriptor(int hintOffset, int startOffset, int endOffset) {
        this.hintOffset = hintOffset;
        this.foldingStartOffset = startOffset;
        this.foldingEndOffset = endOffset;
    }

    public boolean hasHintText() {
        return hintText != null;
    }

    public boolean isHintTextUnited() {
        return isHintTextUnited;
    }

    public void addHintText(String text) {
        this.hintText = text;
    }

    public void addUnitedHintText(String text) {
        isHintTextUnited = true;
        this.hintText = text;
    }

    @NotNull
    public String getHintText() {
        assert hasHintText() : "Hint text was not added";
        return hintText;
    }

    public int getHintOffset() {
        return hintOffset;
    }

    public int getFoldingStartOffset() {
        return foldingStartOffset;
    }

    public int getFoldingEndOffset() {
        return foldingEndOffset;
    }

    /**
     * Serializes to String.
     */
    @NotNull
    public String toString() {
        return hintOffset + "/" + foldingStartOffset + "/" + foldingEndOffset;
    }

    /**
     * Deserializes from String.
     */
    @Nullable
    public static FoldingDescriptor fromString(@NotNull String value) {
        if (value.isEmpty()) {
            return null;
        }
        String[] values = value.split("/");
        return new FoldingDescriptor(
                Integer.parseInt(values[0]),
                Integer.parseInt(values[1]),
                Integer.parseInt(values[2])
        );
    }
}

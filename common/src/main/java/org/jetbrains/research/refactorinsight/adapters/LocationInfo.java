package org.jetbrains.research.refactorinsight.adapters;

public class LocationInfo {
    private final String filePath;
    private final int startOffset;
    private final int endOffset;
    private final int length;
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    public LocationInfo(String filePath, int startOffset, int endOffset, int startLine,
                        int endLine, int startColumn, int endColumn, int length) {
        this.filePath = filePath;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.startLine = startLine;
        this.endLine = endLine;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.length = length;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public int getLength() {
        return length;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }
}

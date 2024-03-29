package org.jetbrains.research.refactorinsight.adapters;

public class CodeRange {
    private final int startLine;
    private final int endLine;
    private final int startColumn;
    private final int endColumn;
    private final String filePath;

    public CodeRange(int startLine, int endLine, int startColumn, int endColumn, String filePath) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.filePath = filePath;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public String getFilePath() {
        return filePath;
    }
}

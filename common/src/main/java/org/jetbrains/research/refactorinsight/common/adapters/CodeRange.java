package org.jetbrains.research.refactorinsight.common.adapters;

public class CodeRange {
  private final int startLine;
  private final int endLine;
  private final int startColumn;
  private final int endColumn;
  private final String filePath;

  /**
   * Creates a wrapper for CodeRange instance provided by RefactoringMiner.
   *
   * @param codeRange from RefactoringMiner.
   */
  public CodeRange(gr.uom.java.xmi.diff.CodeRange codeRange) {
    this.startLine = codeRange.getStartLine();
    this.endLine = codeRange.getEndLine();
    this.startColumn = codeRange.getStartColumn();
    this.endColumn = codeRange.getEndColumn();
    this.filePath = codeRange.getFilePath();
  }

  /**
   * Creates a wrapper for CodeRange instance provided by kotlinRMiner.
   *
   * @param codeRange from kotlinRMiner.
   */
  public CodeRange(org.jetbrains.research.kotlinrminer.diff.CodeRange codeRange) {
    this.startLine = codeRange.getStartLine();
    this.endLine = codeRange.getEndLine();
    this.startColumn = codeRange.getStartColumn();
    this.endColumn = codeRange.getEndColumn();
    this.filePath = codeRange.getFilePath();
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

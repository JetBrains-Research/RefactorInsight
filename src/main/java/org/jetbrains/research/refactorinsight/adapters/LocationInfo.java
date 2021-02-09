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

  /**
   * Creates a wrapper for LocationInfo instance provided by RefactoringMiner.
   *
   * @param locationInfo form RefactoringMiner.
   */
  public LocationInfo(gr.uom.java.xmi.LocationInfo locationInfo) {
    this.filePath = locationInfo.getFilePath();
    this.startOffset = locationInfo.getStartOffset();
    this.endOffset = locationInfo.getEndOffset();
    this.startLine = locationInfo.getStartLine();
    this.endLine = locationInfo.getEndLine();
    this.startColumn = locationInfo.getStartColumn();
    this.endColumn = locationInfo.getEndColumn();
    this.length = locationInfo.getLength();
  }

  /**
   * Creates a wrapper for LocationInfo instance provided by kotlinRMiner.
   *
   * @param locationInfo from kotlinRMiner.
   */
  public LocationInfo(org.jetbrains.research.kotlinrminer.decomposition.LocationInfo locationInfo) {
    this.filePath = locationInfo.getFilePath();
    this.startOffset = locationInfo.getStartOffset();
    this.endOffset = locationInfo.getEndOffset();
    this.startLine = locationInfo.getStartLine();
    this.endLine = locationInfo.getEndLine();
    this.startColumn = locationInfo.getStartColumn();
    this.endColumn = locationInfo.getEndColumn();
    this.length = locationInfo.getLength();
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

public class MethodData {

  private String name;
  private int startOffset;
  private int endOffset;

  /**
   * Constructor for MethodData class.
   * @param name of the method.
   * @param startOffset the start line of the method.
   * @param endOffset the end line of the method.
   */
  public MethodData(String name, int startOffset, int endOffset) {
    this.name = name;
    this.startOffset = startOffset;
    this.endOffset = endOffset;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public String getName() {
    return name;
  }
}

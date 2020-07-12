package org.jetbrains.research.refactorinsight.data.diff;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.openapi.util.Pair;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.ui.windows.DiffWindow;
import  org.jetbrains.research.refactorinsight.utils.StringUtils;

/**
 * Generates data for refactorings needing more than three editors to visualize.
 *
 */
public class MoreSidedDiffRequestGenerator extends DiffRequestGenerator {

  List<MoreSidedRange> lines;

  public MoreSidedDiffRequestGenerator(List<MoreSidedRange> lines) {
    this.lines = lines;
  }

  public MoreSidedDiffRequestGenerator() {
  }

  /**
   * Extracts and returns class names (incl. package) from the left paths.
   *
   * @return list of class names
   */
  public List<String> getClassNames() {
    return lines.stream()
        .map(MoreSidedRange::getLeftPath)
        .map(StringUtils::pathToClassName)
        .distinct()
        .collect(Collectors.toList());
  }

  public List<MoreSidedRange> getLines() {
    return lines;
  }

  /**
   * Serializer.
   *
   * @return new MoreSidedDiffRequestGenerator from string
   */
  public static MoreSidedDiffRequestGenerator fromString(String seq) {
    List<MoreSidedRange> lines = Arrays.stream(seq.split(StringUtils.delimiter(StringUtils.LIST)))
        .map(MoreSidedRange::fromString).collect(Collectors.toList());

    return new MoreSidedDiffRequestGenerator(lines);
  }

  @Override
  public SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info) {
    assert contents.length == lines.size() + 1;
    for (int i = 0; i < lines.size(); i++) {
      lines.get(i).content = contents[i + 1];
    }
    SimpleDiffRequest request = new SimpleDiffRequest(info.getName(),
        contents[1], contents[0], "Subclasses", info.getRightPath());
    request.putUserData(DiffWindow.REFACTORING, true);
    request.putUserData(DiffWindow.MORESIDED_RANGES, lines);
    request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
        (text1, text2, policy, innerChanges, indicator)
            -> new ArrayList<>());
    return request;
  }

  @Override
  public void prepareJetBrainsRanges(List<RefactoringLine> lineMarkings) {
    lines = lineMarkings.stream()
        .map(RefactoringLine::getMoreSidedRange).collect(Collectors.toList());
  }

  @Override
  public void addMarking(CodeRange left, CodeRange mid, CodeRange right,
                         RefactoringLine.VisualisationType type,
                         Consumer<RefactoringLine> offsetFunction,
                         RefactoringLine.MarkingOption option,
                         boolean hasColumns) {
    RefactoringLine line = new RefactoringLine(left, mid, right, type, option, hasColumns, true);
    if (offsetFunction != null) {
      offsetFunction.accept(line);
    }
    lineMarkings.add(line);
  }

  /**
   * Correct method that is NOT compatible with more sided ranges.
   * Throws Exception if called.
   */
  @Override
  public void correct(String before, String mid, String after, boolean skipAnnotationsLeft,
                      boolean skipAnnotationsMid, boolean skipAnnotationsRight) {
    throw new IllegalStateException("Incorrect correct method for more sided diff request");
  }

  /**
   * Compatible correct method for more sided ranges.
   * Corrects lines and offsets.
   *
   * @param befores  All texts of left window (need to be in order!)
   * @param after    Text of right side
   * @param pathPair Path of file and boolean for revision
   */
  public void correct(List<String> befores, String after, List<Pair<String, Boolean>> pathPair,
                      boolean skipAnnotationsLeft,
                      boolean skipAnnotationsMid, boolean skipAnnotationsRight) {
    assert pathPair.size() == lineMarkings.size();
    for (int i = 0; i < befores.size(); i++) {
      lineMarkings.get(i).correctLines(befores.get(i), null, after, skipAnnotationsLeft,
          skipAnnotationsMid, skipAnnotationsRight);
      lineMarkings.get(i).getMoreSidedRange().leftPath = pathPair.get(i).first;
      if (pathPair.get(i).second) {
        lineMarkings.get(i).getMoreSidedRange().startLineRight = -1;
        lineMarkings.get(i).getMoreSidedRange().endLineRight = -1;
      }
    }
    prepareJetBrainsRanges(lineMarkings);
  }

  @Override
  public String toString() {
    if (lines == null || lines.size() == 0) {
      return "";
    }
    return lines.stream().map(MoreSidedRange::toString)
        .collect(Collectors.joining(StringUtils.delimiter(StringUtils.LIST)));
  }

  public static class MoreSidedRange implements Comparable<MoreSidedRange> {
    public int startLineLeft;
    public int endLineLeft;
    public int startOffsetLeft;
    public int endOffsetLeft;
    public int startLineRight;
    public int endLineRight;
    public int startOffsetRight;
    public int endOffsetRight;
    public String leftPath;
    public transient DiffContent content;

    /**
     * Constructor for Data.
     *
     * @param startLineLeft    int
     * @param endLineLeft      int
     * @param startOffsetLeft  int
     * @param endOffsetLeft    int
     * @param startLineRight   int
     * @param endLineRight     int
     * @param startOffsetRight int
     * @param endOffsetRight   int
     * @param leftPath         path
     */
    public MoreSidedRange(int startLineLeft, int endLineLeft,
                          int startOffsetLeft, int endOffsetLeft,
                          int startLineRight, int endLineRight,
                          int startOffsetRight, int endOffsetRight,
                          String leftPath) {
      this.startLineLeft = startLineLeft;
      this.endLineLeft = endLineLeft;
      this.startOffsetLeft = startOffsetLeft;
      this.endOffsetLeft = endOffsetLeft;
      this.startLineRight = startLineRight;
      this.endLineRight = endLineRight;
      this.startOffsetRight = startOffsetRight;
      this.endOffsetRight = endOffsetRight;
      this.leftPath = leftPath;
    }

    public MoreSidedRange() {
    }

    public String getLeftPath() {
      return leftPath;
    }

    /**
     * Deserializer.
     */
    public static MoreSidedRange fromString(String seq) {
      String value = StringUtils.deSanitize(seq);
      String[] tokens = value.split(StringUtils.delimiter(StringUtils.RANGE));
      MoreSidedRange moreSidedRange = new MoreSidedRange();
      moreSidedRange.startLineLeft = Integer.parseInt(tokens[0]);
      moreSidedRange.endLineLeft = Integer.parseInt(tokens[1]);
      moreSidedRange.startOffsetLeft = Integer.parseInt(tokens[2]);
      moreSidedRange.endOffsetLeft = Integer.parseInt(tokens[3]);
      moreSidedRange.startLineRight = Integer.parseInt(tokens[4]);
      moreSidedRange.endLineRight = Integer.parseInt(tokens[5]);
      moreSidedRange.startOffsetRight = Integer.parseInt(tokens[6]);
      moreSidedRange.endOffsetRight = Integer.parseInt(tokens[7]);
      moreSidedRange.leftPath = tokens[8];
      return moreSidedRange;
    }

    /**
     * Serializer.
     */
    public String toString() {
      return String.join(StringUtils.delimiter(StringUtils.RANGE),
          StringUtils.sanitize(Integer.toString(startLineLeft)),
          StringUtils.sanitize(Integer.toString(endLineLeft)),
          StringUtils.sanitize(Integer.toString(startOffsetLeft)),
          StringUtils.sanitize(Integer.toString(endOffsetLeft)),
          StringUtils.sanitize(Integer.toString(startLineRight)),
          StringUtils.sanitize(Integer.toString(endLineRight)),
          StringUtils.sanitize(Integer.toString(startOffsetRight)),
          StringUtils.sanitize(Integer.toString(endOffsetRight)),
          StringUtils.sanitize(leftPath));
    }


    @Override
    public int compareTo(@NotNull MoreSidedRange moreSidedRange) {
      if (leftPath.equals(moreSidedRange.leftPath)) {
        if (startLineLeft == moreSidedRange.startLineLeft) {
          return 0;
        } else if (startLineLeft > moreSidedRange.startLineLeft) {
          return 1;
        }
        return -1;
      }
      return leftPath.compareTo(moreSidedRange.leftPath);
    }
  }

}

package data.diff;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.openapi.util.Pair;
import data.RefactoringInfo;
import data.RefactoringLine;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import ui.windows.DiffWindow;
import utils.StringUtils;

public class MoreSidedDiffRequestGenerator extends DiffRequestGenerator {

  List<Data> lines;

  public MoreSidedDiffRequestGenerator(List<Data> lines) {
    this.lines = lines;
  }

  public MoreSidedDiffRequestGenerator() {
  }

  public List<Data> getLines() {
    return lines;
  }

  /**
   * Serializer.
   * @return this
   */
  public static MoreSidedDiffRequestGenerator fromString(String seq) {
    List<Data> lines = Arrays.asList(seq.split(StringUtils.delimiter(StringUtils.LIST))).stream()
        .map(dataSt -> Data.fromString(dataSt)).collect(Collectors.toList());

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
        .map(RefactoringLine::getMoreSidedData).collect(Collectors.toList());
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
   * @param befores All texts of left window (need to be in order!)
   * @param after Text of right side
   * @param pathPair Path of file and boolean for revision
   */
  public void correct(List<String> befores, String after, List<Pair<String, Boolean>> pathPair,
                      boolean skipAnnotationsLeft,
                      boolean skipAnnotationsMid, boolean skipAnnotationsRight) {
    assert pathPair.size() == lineMarkings.size();
    for (int i = 0; i < befores.size(); i++) {
      lineMarkings.get(i).correctLines(befores.get(i), null, after, skipAnnotationsLeft,
          skipAnnotationsMid, skipAnnotationsRight);
      lineMarkings.get(i).getMoreSidedData().leftPath = pathPair.get(i).first;
      if (pathPair.get(i).second) {
        lineMarkings.get(i).getMoreSidedData().startLineRight = -1;
        lineMarkings.get(i).getMoreSidedData().endLineRight = -1;
      }
    }
    prepareJetBrainsRanges(lineMarkings);
  }

  @Override
  public String toString() {
    if (lines == null || lines.size() == 0) {
      return "";
    }
    return lines.stream().map(Data::toString)
        .collect(Collectors.joining(StringUtils.delimiter(StringUtils.LIST)));
  }

  public static class Data implements Comparable<Data> {
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
     * Deserializer.
     */
    public static Data fromString(String seq) {
      String value = StringUtils.deSanitize(seq);
      String[] tokens = value.split(StringUtils.delimiter(StringUtils.RANGE));
      Data data = new Data();
      data.startLineLeft = Integer.parseInt(tokens[0]);
      data.endLineLeft = Integer.parseInt(tokens[1]);
      data.startOffsetLeft = Integer.parseInt(tokens[2]);
      data.endOffsetLeft = Integer.parseInt(tokens[3]);
      data.startLineRight = Integer.parseInt(tokens[4]);
      data.endLineRight = Integer.parseInt(tokens[5]);
      data.startOffsetRight = Integer.parseInt(tokens[6]);
      data.endOffsetRight = Integer.parseInt(tokens[7]);
      data.leftPath = tokens[8];
      return data;
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
          leftPath);
    }


    @Override
    public int compareTo(@NotNull Data data) {
      if (leftPath.equals(data.leftPath)) {
        if (startLineLeft == data.startLineLeft) {
          return 0;
        } else if (startLineLeft > data.startLineLeft) {
          return 1;
        }
        return -1;
      }
      return leftPath.compareTo(data.leftPath);
    }
  }

}

package data.diff;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import data.RefactoringInfo;
import data.RefactoringLine;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import ui.windows.DiffWindow;
import utils.StringUtils;

public class MoreSidedDiffRequestGenerator extends DiffRequestGenerator {

  List<Data> lines;

  public MoreSidedDiffRequestGenerator(List<Data> lines) {
    this.lines = lines;
  }

  public MoreSidedDiffRequestGenerator() {}

  @Override
  public SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info) {
    assert contents.length == lines.size();
    for (int i = 0; i < contents.length; i++) {
      lines.get(i).content = contents[i];
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
    lines = lineMarkings.stream().map(RefactoringLine::getMoreSidedData).collect(Collectors.toList());
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

  @Override
  public void correct(String before, String mid, String after) {
    throw new IllegalStateException("Incorrect correct method for more sided diff request");
  }

  public void correct(List<String> befores, String after) {
    lineMarkings.get(0).correctLines(null, null, after);
    for (int i = 0; i < befores.size(); i++) {
      lineMarkings.get(i + 1).correctLines(befores.get(i), null, null);
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

  public static MoreSidedDiffRequestGenerator fromString(String seq) {
    List<Data> lines = Arrays.asList(seq.split(StringUtils.delimiter(StringUtils.LIST))).stream()
    .map(dataSt -> Data.fromString(dataSt)).collect(Collectors.toList());

    return new MoreSidedDiffRequestGenerator(lines);
  }

  public static class Data {
    public int startLine;
    public int endLine;
    public int startOffset;
    public int endOffset;
    public transient DiffContent content;

    public Data(int startLine, int endLine, int startOffset, int endOffset) {
      this.startLine = startLine;
      this.endLine = endLine;
      this.startOffset = startOffset;
      this.endOffset = endOffset;
    }

    public String toString() {
      String del = StringUtils.delimiter(StringUtils.FRAG);
      return startLine + del + endLine + del + startOffset + del + endOffset;
    }

    public static Data fromString(String seq) {
      String[] tokens = seq.split(StringUtils.delimiter(StringUtils.FRAG));
      return new Data(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]),
          Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
    }


  }

}

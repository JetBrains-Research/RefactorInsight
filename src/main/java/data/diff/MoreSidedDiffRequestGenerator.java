package data.diff;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import data.RefactoringInfo;
import data.RefactoringLine;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import ui.windows.DiffWindow;

public class MoreSidedDiffRequestGenerator extends DiffRequestGenerator {

  List<Data> lines;

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
  }

}

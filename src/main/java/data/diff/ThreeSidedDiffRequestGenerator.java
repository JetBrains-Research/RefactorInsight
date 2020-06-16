package data.diff;

import static ui.windows.DiffWindow.REFACTORING;
import static ui.windows.DiffWindow.THREESIDED_RANGES;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import data.RefactoringInfo;
import data.RefactoringLine;
import java.util.List;
import java.util.stream.Collectors;

public class ThreeSidedDiffRequestGenerator extends DiffRequestGenerator {

  private List<ThreeSidedRange> ranges;

  public ThreeSidedDiffRequestGenerator() {
  }

  @Override
  public SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info) {
    SimpleDiffRequest request = new SimpleDiffRequest(info.getName(),
        contents[0], contents[1], contents[2],
        info.getLeftPath(), info.getMidPath(), info.getRightPath());
    request.putUserData(THREESIDED_RANGES, ranges);
    request.putUserData(REFACTORING, true);
    return request;
  }

  @Override
  public void prepareJetBrainsRanges(List<RefactoringLine> lineMarkings) {
    ranges = lineMarkings.stream().map(RefactoringLine::getThreeSidedRange)
        .collect(Collectors.toList());
  }
}

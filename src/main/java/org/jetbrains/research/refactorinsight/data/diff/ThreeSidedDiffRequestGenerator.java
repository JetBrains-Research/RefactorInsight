package org.jetbrains.research.refactorinsight.data.diff;

import static org.jetbrains.research.refactorinsight.ui.windows.DiffWindow.REFACTORING;
import static org.jetbrains.research.refactorinsight.ui.windows.DiffWindow.THREESIDED_RANGES;
import static org.jetbrains.research.refactorinsight.utils.StringUtils.LIST;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.utils.StringUtils;

/**
 * Creates ThreeSidedRanges out of RefactoringLines in order to create three sided diff windows.
 */
public class ThreeSidedDiffRequestGenerator extends DiffRequestGenerator {

  private List<ThreeSidedRange> ranges = new ArrayList<>();

  public ThreeSidedDiffRequestGenerator() {
  }

  /**
   * Serializes a ThreeSidedDiffRequestGenerator.
   *
   * @return the string value
   */
  public static ThreeSidedDiffRequestGenerator fromString(String value) {
    String regex = StringUtils.delimiter(LIST, true);
    ThreeSidedDiffRequestGenerator generator = new ThreeSidedDiffRequestGenerator();
    String[] tokens = value.split(regex);
    if (tokens[0].isEmpty()) {
      return generator;
    }
    generator.ranges = Arrays.stream(tokens)
        .map(ThreeSidedRange::fromString).collect(Collectors.toList());
    return generator;
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

  @Override
  public String toString() {
    return ranges.stream().map(ThreeSidedRange::toString)
        .collect(Collectors.joining(StringUtils.delimiter(LIST)));
  }
}

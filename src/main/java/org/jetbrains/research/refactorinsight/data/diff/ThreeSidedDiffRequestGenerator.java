package org.jetbrains.research.refactorinsight.data.diff;

import static org.jetbrains.research.refactorinsight.ui.windows.DiffWindow.REFACTORING;
import static org.jetbrains.research.refactorinsight.ui.windows.DiffWindow.THREESIDED_RANGES;
import static org.jetbrains.research.refactorinsight.utils.StringUtils.LIST;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.intellij.diff.util.ThreeSide;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.utils.StringUtils;

/**
 * Creates an {@link ThreeSidedRange} instance for three sided diff windows.
 */
public class ThreeSidedDiffRequestGenerator extends DiffRequestGenerator {

  private List<ThreeSidedRange> ranges = new ArrayList<>();

  public ThreeSidedDiffRequestGenerator() {
  }

  /**
   * Deserializes an {@link ThreeSidedDiffRequestGenerator} instance.
   *
   * @param value string value.
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
  public void prepareRanges(List<RefactoringLine> lineMarkings) {
    ranges = lineMarkings.stream().map(RefactoringLine::getThreeSidedRange)
        .collect(Collectors.toList());
  }

  @Override
  public boolean containsElement(int lineNumber, int textOffset, boolean isRight) {
    Function<ThreeSidedRange, Integer> startLineExtractor = isRight
            ? r -> r.fragment.getStartLine(ThreeSide.RIGHT)
            : r -> r.fragment.getStartLine(ThreeSide.LEFT);
    List<Integer> startLines = ranges.stream().map(startLineExtractor).toList();
    //TODO: check textOffset
    return startLines.contains(lineNumber);
  }

  @Override
  public String toString() {
    return ranges.stream().map(ThreeSidedRange::toString)
        .collect(Collectors.joining(StringUtils.delimiter(LIST)));
  }
}

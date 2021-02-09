package org.jetbrains.research.refactorinsight.data.diff;

import static org.jetbrains.research.refactorinsight.ui.windows.DiffWindow.REFACTORING;
import static org.jetbrains.research.refactorinsight.utils.StringUtils.FRAG;
import static org.jetbrains.research.refactorinsight.utils.StringUtils.LIST;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.fragments.DiffFragment;
import com.intellij.diff.fragments.DiffFragmentImpl;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.data.RefactoringLine;
import org.jetbrains.research.refactorinsight.utils.StringUtils;

/**
 * Converts {@link RefactoringLine} instances into {@link LineFragment} objects to create two sided diff window.
 */
public class TwoSidedDiffRequestGenerator extends DiffRequestGenerator {

  public List<LineFragment> fragments = new ArrayList<>();

  public TwoSidedDiffRequestGenerator() {
  }

  public TwoSidedDiffRequestGenerator(List<LineFragment> frags) {
    fragments = frags;
  }

  /**
   * Deserializes an {@link TwoSidedDiffRequestGenerator} instance.
   *
   * @param value string.
   * @return the TwoSidedDiffRequestGenerator.
   */
  public static TwoSidedDiffRequestGenerator fromString(String value) {
    String regex1 = StringUtils.delimiter(LIST, true);
    String regex2 = StringUtils.delimiter(FRAG, true);
    String[] tokens = value.split(regex1);
    TwoSidedDiffRequestGenerator generator = new TwoSidedDiffRequestGenerator();
    if (value.isEmpty()) {
      return generator;
    }
    generator.fragments = Arrays.stream(tokens).map(string -> {
      String[] toks = string.split(regex2, 9);
      String[] diffs = toks[8].split(regex2);
      List<DiffFragment> frags = diffs[0].isEmpty() ? new ArrayList<>()
          : IntStream.range(0, diffs.length / 4).map(i -> i * 4).mapToObj(i -> new DiffFragmentImpl(
          Integer.parseInt(diffs[i]),
          Integer.parseInt(diffs[i + 1]),
          Integer.parseInt(diffs[i + 2]),
          Integer.parseInt(diffs[i + 3])
      )).collect(Collectors.toList());
      return new LineFragmentImpl(
          Integer.parseInt(toks[0]), Integer.parseInt(toks[1]),
          Integer.parseInt(toks[2]), Integer.parseInt(toks[3]),
          Integer.parseInt(toks[4]), Integer.parseInt(toks[5]),
          Integer.parseInt(toks[6]), Integer.parseInt(toks[7]),
          frags
      );
    }).collect(Collectors.toList());
    return generator;
  }

  @Override
  public SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info) {
    SimpleDiffRequest request;
    if (contents[0] == null || contents[2] == null) {
      return null;
    }
    request = new SimpleDiffRequest(info.getName(),
                                    contents[0], contents[2], info.getLeftPath(), info.getRightPath());
    request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
                        (text1, text2, policy, innerChanges, indicator)
                            -> fragments);
    request.putUserData(REFACTORING, true);
    return request;
  }

  @Override
  public void prepareRanges(List<RefactoringLine> lineMarkings) {
    fragments = lineMarkings.stream()
        .map(RefactoringLine::getTwoSidedRange)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Serializes an {@link TwoSidedDiffRequestGenerator} instance.
   *
   * @return the string value.
   */
  public String toString() {
    if (fragments == null) {
      return "";
    }
    String del = StringUtils.delimiter(FRAG);
    return fragments.stream().map(frag ->
        Stream.of(
            frag.getStartLine1(),
            frag.getEndLine1(),
            frag.getStartLine2(),
            frag.getEndLine2(),
            frag.getStartOffset1(),
            frag.getEndOffset1(),
            frag.getStartOffset2(),
            frag.getEndOffset2(),
            (frag.getInnerFragments() == null ? ""
                : frag.getInnerFragments().stream().map(f ->
                f.getStartOffset1() + del
                    + f.getEndOffset1() + del
                    + f.getStartOffset2() + del
                    + f.getEndOffset2()
            ).collect(Collectors.joining(del)))
        ).map(String::valueOf).collect(Collectors.joining(del))
    ).collect(Collectors.joining(StringUtils.delimiter(LIST)));
  }
}

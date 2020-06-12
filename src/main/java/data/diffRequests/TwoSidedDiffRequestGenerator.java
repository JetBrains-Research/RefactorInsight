package data.diffRequests;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.fragments.DiffFragment;
import com.intellij.diff.fragments.DiffFragmentImpl;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import data.RefactoringInfo;
import data.RefactoringLine;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TwoSidedDiffRequestGenerator extends DiffRequestGenerator {

  public List<LineFragment> fragments;

  public TwoSidedDiffRequestGenerator() {
  }

  @Override
  public SimpleDiffRequest generate(DiffContent[] contents, RefactoringInfo info) {
    SimpleDiffRequest request;
    request = new SimpleDiffRequest(info.getName(),
        contents[0], contents[2], info.getLeftPath(), info.getRightPath());
    request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
        (text1, text2, policy, innerChanges, indicator)
            -> fragments);
    return request;
  }

  @Override
  public void correct(String before, String mid, String after) {
    super.correct(before, mid, after);
    fragments = lineMarkings.stream()
        .map(RefactoringLine::getTwoSidedRange)
        .collect(Collectors.toList());
  }

  public String toString() {
    if (fragments == null) {
      return "null";
    }
    return fragments.stream().map(frag ->
        frag.getStartLine1() + fragDelimiter
            + frag.getEndLine1() + fragDelimiter
            + frag.getStartLine2() + fragDelimiter
            + frag.getEndLine2() + fragDelimiter
            + frag.getStartOffset1() + fragDelimiter
            + frag.getEndOffset1() + fragDelimiter
            + frag.getStartOffset2() + fragDelimiter
            + frag.getEndOffset2() + fragDelimiter
            + (frag.getInnerFragments() == null ? "null"
            : frag.getInnerFragments().stream().map(f ->
            f.getStartOffset1() + fragDelimiter
                + f.getEndOffset1() + fragDelimiter
                + f.getStartOffset2() + fragDelimiter
                + f.getEndOffset2()
        ).collect(Collectors.joining(fragDelimiter)))
    ).collect(Collectors.joining(listDelimiter));
  }

  public static TwoSidedDiffRequestGenerator fromString(String value) {
    String[] tokens = value.split(listDelimiter);
    TwoSidedDiffRequestGenerator generator = new TwoSidedDiffRequestGenerator();
    if (value.equals("null")) {
      return generator;
    }
    generator.fragments = Arrays.stream(tokens).map(string -> {
      String[] toks = string.split(fragDelimiter, 9);
      String[] diffs = toks[8].split(fragDelimiter);
      List<DiffFragment> frags = toks[8].equals("null") ? null
          : IntStream.range(0, diffs.length / 4).map(i -> i * 4).mapToObj(i -> new DiffFragmentImpl(
          Integer.parseInt(diffs[i]),
          Integer.parseInt(diffs[i + 1]),
          Integer.parseInt(diffs[i + 2]),
          Integer.parseInt(diffs[i + 3])
      )).collect(Collectors.toList());
      assert diffs.length % 4 == 0;
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
}

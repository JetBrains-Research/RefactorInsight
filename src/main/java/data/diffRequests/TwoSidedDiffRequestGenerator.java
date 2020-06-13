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
import java.util.stream.Stream;
import utils.Utils;

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
        Stream.of(
            frag.getStartLine1(),
            frag.getEndLine1(),
            frag.getStartLine2(),
            frag.getEndLine2(),
            frag.getStartOffset1(),
            frag.getEndOffset1(),
            frag.getStartOffset2(),
            frag.getEndOffset2(),
            (frag.getInnerFragments() == null ? "null"
                : frag.getInnerFragments().stream().map(f ->
                f.getStartOffset1() + Utils.FRAG_DELIMITER
                    + f.getEndOffset1() + Utils.FRAG_DELIMITER
                    + f.getStartOffset2() + Utils.FRAG_DELIMITER
                    + f.getEndOffset2()
            ).collect(Collectors.joining(Utils.FRAG_DELIMITER)))
        ).map(String::valueOf).collect(Collectors.joining(Utils.FRAG_DELIMITER))
    ).collect(Collectors.joining(Utils.LIST_DELIMITER));
  }

  public static TwoSidedDiffRequestGenerator fromString(String value) {
    String[] tokens = value.split(Utils.LIST_DELIMITER);
    TwoSidedDiffRequestGenerator generator = new TwoSidedDiffRequestGenerator();
    if (value.equals("null")) {
      return generator;
    }
    generator.fragments = Arrays.stream(tokens).map(string -> {
      String[] toks = string.split(Utils.FRAG_DELIMITER, 9);
      String[] diffs = toks[8].split(Utils.FRAG_DELIMITER);
      List<DiffFragment> frags = toks[8].equals("null") || diffs[0].isEmpty() ? null
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
}

import com.google.common.collect.Streams;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.fragments.LineFragmentImpl;
import com.intellij.openapi.vcs.VcsException;
import java.util.List;
import java.util.stream.Collectors;

public class FileDiffInfo {

  private String leftPath;
  private String rightPath;
  private String leftContent;
  private String rightContent;
  private List<LineFragment> diffFragments;

  /**
   * Constructor for FileDifInfo from two HalfDiffInfo's.
   *
   * @param left  HalfDiffInfo
   * @param right HalfDiffInfo
   */
  public FileDiffInfo(HalfDiffInfo left, HalfDiffInfo right) {
    this.leftPath = left.getRevision().getFile().getPath();
    this.rightPath = right.getRevision().getFile().getPath();
    try {
      this.leftContent = left.getRevision().getContent();
      this.rightContent = right.getRevision().getContent();
    } catch (VcsException e) {
      e.printStackTrace();
    }
    this.diffFragments = Streams.zip(
        left.getRanges().stream(),
        right.getRanges().stream(),
        (l, r) -> new LineFragmentImpl(
            l.start, l.end < 0
            ? (int) leftContent.chars().filter(x -> x == '\n').count() + 1 : l.end,
            r.start, r.end < 0
            ? (int) rightContent.chars().filter(x -> x == '\n').count() + 1 : l.end,
            0, 0, 0, 0))
        .collect(Collectors.toList());
  }

  public String getLeftPath() {
    return leftPath;
  }

  public String getRightPath() {
    return rightPath;
  }

  public String getLeftContent() {
    return leftContent;
  }

  public String getRightContent() {
    return rightContent;
  }

  public List<LineFragment> getDiffFragments() {
    return diffFragments;
  }
}

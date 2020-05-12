import com.intellij.openapi.vcs.changes.ContentRevision;
import java.util.List;

public class HalfDiffInfo {

  private List<LineRange> ranges;
  private ContentRevision revision;

  public HalfDiffInfo(List<LineRange> ranges, ContentRevision revision) {
    this.ranges = ranges;
    this.revision = revision;
  }

  public List<LineRange> getRanges() {
    return ranges;
  }

  public ContentRevision getRevision() {
    return revision;
  }

  @Override
  public String toString() {
    return revision.getFile().getPath();
  }
}

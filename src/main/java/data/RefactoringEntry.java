package data;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class RefactoringEntry implements Serializable {

  private List<RefactoringInfo> data;
  private List<String> parents;
  private String commitId;
  private long time;

  /**
   * Constructor for method refactoring.
   *
   * @param data    the refactoring data.
   * @param parents the commit ids of the parents.
   * @param time    timestamp of the commit.
   */
  public RefactoringEntry(List<RefactoringInfo> data, String commitId, List<String> parents,
                          long time) {
    this.data = data;
    this.parents = parents;
    this.time = time;
    this.commitId = commitId;
  }

  /**
   * Deserialize a refactoring info json.
   *
   * @param value json string
   * @return a new data.RefactoringInfo object
   */
  public static RefactoringEntry fromString(String value) {
    if (value == null || value.equals("")) {
      return null;
    }
    try {
      return new Gson().fromJson(value, RefactoringEntry.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Converter to Json.
   *
   * @param refactorings to be processed.
   * @param commitId     current commit.
   * @param parents      parent ids of the current commit.
   * @param time         timestamp of the current commit.
   * @return Json string.
   */
  public static String convert(List<Refactoring> refactorings, String commitId,
                               List<String> parents, long time) {
    return new RefactoringEntry(
        refactorings.stream()
            .map(refactoring -> new RefactoringInfo(refactoring, commitId))
            .collect(Collectors.toList()),
        commitId, parents, time).toString();
  }

  public List<RefactoringInfo> getRefactorings() {
    return data;
  }

  public List<String> getParents() {
    return parents;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public long getTimeStamp() {
    return time;
  }

  public String getCommitId() {
    return commitId;
  }
}

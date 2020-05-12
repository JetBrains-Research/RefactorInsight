import com.google.gson.Gson;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class RefactoringEntry implements Serializable {

  private List<RefactoringInfo> data;
  private String parentCommit;

  /**
   * Constructor for method refactoring.
   *
   * @param data         the refactoring data.
   * @param parentCommit the commit id as a Hash.
   */
  public RefactoringEntry(List<RefactoringInfo> data, String parentCommit) {
    this.data = data;
    this.parentCommit = parentCommit;
  }

  /**
   * Deserialize a refactoring info json.
   *
   * @param value json string
   * @return a new RefactoringInfo object
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
   * @param refactorings to be processed.
   * @param commitId current commit.
   * @param parentCommit parent id of the current commit.
   * @return Json string.
   */
  public static String convert(List<Refactoring> refactorings, String commitId,
                               String parentCommit) {
    return new RefactoringEntry(
        refactorings.stream()
            .map(refactoring -> new RefactoringInfo(refactoring, commitId))
            .collect(Collectors.toList()),
        parentCommit).toString();
  }

  public List<RefactoringInfo> getRefactorings() {
    return data;
  }

  public String getParentCommit() {
    return parentCommit;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

}

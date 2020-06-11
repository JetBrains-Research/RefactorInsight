package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryData {

  private List<RefactoringInfo> refactoringInfoList;
  private Set<String> namesBefore;

  public HistoryData() {
    namesBefore = new HashSet<>();
    refactoringInfoList = new ArrayList<>();
  }

  /**
   * Adds refactoring infos objects to the list if not already present.
   *
   * @param infos to be added.
   */
  public void addRefactorings(List<RefactoringInfo> infos) {
    for (RefactoringInfo info : infos) {
      if (!refactoringInfoList.contains(info)) {
        refactoringInfoList.add(info);
      }
    }
  }

  public void addOldName(String oldName) {
    namesBefore.add(oldName);
  }

  public List<RefactoringInfo> getRefactoringInfoList() {
    return refactoringInfoList;
  }

  public Set<String> getNamesBefore() {
    return namesBefore;
  }

  public void addRefactoring(RefactoringInfo info) {
    refactoringInfoList.add(info);
  }
}

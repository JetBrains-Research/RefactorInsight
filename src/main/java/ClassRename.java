public class ClassRename {

  private String classBefore;
  private String classAfter;
  private long commitTime;

  /**
   * Constructor for class renaming refactoring.
   * @param classBefore name of the class before renaming
   * @param classAfter name of the class after renaming
   * @param commitTime time of the commit
   */
  public ClassRename(String classBefore, String classAfter, long commitTime) {
    this.classBefore = classBefore;
    this.classAfter = classAfter;
    this.commitTime = commitTime;
  }

  public String getClassAfter() {
    return classAfter;
  }

  public String getClassBefore() {
    return classBefore;
  }

  public long getCommitTime() {
    return commitTime;
  }
}

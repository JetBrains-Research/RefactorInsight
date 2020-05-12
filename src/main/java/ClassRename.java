public class ClassRename {

  private String classBefore;
  private String classAfter;

  /**
   * Constructor for class renaming refactoring.
   * @param classBefore name of the class before renaming
   * @param classAfter name of the class after renaming
   */
  public ClassRename(String classBefore, String classAfter) {
    this.classBefore = classBefore;
    this.classAfter = classAfter;
  }

  public String getClassAfter() {
    return classAfter;
  }

  public String getClassBefore() {
    return classBefore;
  }

}

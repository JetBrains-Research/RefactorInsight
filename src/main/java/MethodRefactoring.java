import java.io.Serializable;

public class MethodRefactoring implements Serializable {

  private MethodRefactoringData data;
  private String commmitId;
  String file;

  /**
   * Constructor for method refactoring.
   * @param data the refactoring data.
   * @param commmitId the commit id as a Hash.
   */
  public MethodRefactoring(MethodRefactoringData data, String commmitId, String file) {
    this.data = data;
    this.commmitId = commmitId;
    this.file = file;
  }

  public MethodRefactoringData getData() {
    return data;
  }

  public String getCommmitId() {
    return commmitId;
  }

}

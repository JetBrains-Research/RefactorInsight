package refactoringInfo;

import com.google.gson.Gson;
import org.refactoringminer.api.RefactoringType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefactoringInfo {

  private String text;
  private String name;
  private RefactoringType type;
  private List<TrueCodeRange> leftSide;
  private List<TrueCodeRange> rightSide;
  private Map<String, String> renames = new HashMap<>();
  private int[] nameIndeces = {0, 0};

  /**
   * Constructor for refactoring info data structure.
   *
   * @param name of the refactoring
   * @param text of the refactoring
   * @param type of the refactoring
   * @param leftSide before refactoring data
   * @param rightSide after refactoring data
   */
  public RefactoringInfo(String name, String text, RefactoringType type, List<TrueCodeRange> leftSide, List<TrueCodeRange> rightSide) {
    this.name = name;
    this.type = type;
    this.text = text;
    this.leftSide = leftSide;
    this.rightSide = rightSide;
    //refactoring.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList());
    //refactoring.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList());
  }

  public Map<String, String> getRenames() {
    return renames;
  }

  public String getName() {
    return name;
  }

  public String getText() {
    return text;
  }

  public RefactoringType getType() {
    return type;
  }

  public List<TrueCodeRange> getLeftSide() {
    return leftSide;
  }

  public List<TrueCodeRange> getRightSide() {
    return rightSide;
  }

  public int[] getNameIndeces() {
    return nameIndeces;
  }

  public void setNameIndeces(int[] nameIndeces) {
    this.nameIndeces = nameIndeces;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public void setText(String text) {
    this.text = text;
  }

  public RefactoringInfo() {
  }

  /**
   * Deserialize a refactoring info json.
   *
   * @param value json string
   * @return a new refactoringInfo.RefactoringInfo object
   */
  public static RefactoringInfo fromString(String value) {
    if (value == null || value.equals("")) {
      return null;
    }
    try {
      return new Gson().fromJson(value, RefactoringInfo.class);
    } catch (Exception e) {
      e.printStackTrace();
      RefactoringInfo ri = new RefactoringInfo();
      ri.setText("wtf: " + value);
      return ri;
    }
  }

}

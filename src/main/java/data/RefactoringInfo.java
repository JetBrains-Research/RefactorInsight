package data;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.refactoringminer.api.RefactoringType;

public class RefactoringInfo {

  private String text;
  private String name;
  private String commitId;
  private String nameBefore;
  private String nameAfter;
  private RefactoringType type;
  private List<TrueCodeRange> leftSide;
  private List<TrueCodeRange> rightSide;
  private Type object;

  public RefactoringInfo(Type object) {
    this.object = object;
  }

  /**
   * Adds this refactoring to the method history map.
   * Note that it should be called in chronological order.
   *
   * @param map for method history
   */
  public void addToHistory(Map<String, List<RefactoringInfo>> map) {
    //System.out.println(signatureAfter);
    if (object == Type.CLASS && nameBefore != null && nameAfter != null) {
      Map<String, String> renames = new HashMap<>();
      renames.put(nameBefore, nameAfter);
      renames.forEach((before, after) -> map.keySet().stream()
          .filter(x -> x.substring(0, x.lastIndexOf("."))
              .equals(before))
          .forEach(signature -> {
            String newKey = after + signature.substring(signature.lastIndexOf("."));
            map.put(newKey, map.getOrDefault(signature, new ArrayList<>()));
          }));
      return;
    }

    if (object == Type.METHOD) {
      List<RefactoringInfo> refs = map.getOrDefault(nameBefore, new LinkedList<>());
      map.remove(nameBefore);
      refs.add(0, this);
      map.put(nameAfter, refs);
    }
  }


  public String getName() {
    return name;
  }

  public RefactoringInfo setName(String name) {
    this.name = name;
    return this;
  }

  public String getText() {
    return text;
  }

  public RefactoringInfo setText(String text) {
    this.text = text;
    return this;
  }

  public String getCommitId() {
    return commitId;
  }

  public RefactoringInfo setCommitId(String commitId) {
    this.commitId = commitId;
    return this;
  }

  public RefactoringType getType() {
    return type;
  }

  public RefactoringInfo setType(RefactoringType type) {
    this.type = type;
    return this;
  }

  public List<TrueCodeRange> getLeftSide() {
    return leftSide;
  }

  public RefactoringInfo setLeftSide(List<TrueCodeRange> leftSide) {
    this.leftSide = leftSide;
    return this;
  }

  public List<TrueCodeRange> getRightSide() {
    return rightSide;
  }

  public RefactoringInfo setRightSide(List<TrueCodeRange> rightSide) {
    this.rightSide = rightSide;
    return this;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public String getNameBefore() {
    return nameBefore;
  }

  public RefactoringInfo setNameBefore(String nameBefore) {
    this.nameBefore = nameBefore;
    return this;
  }

  public String getNameAfter() {
    return nameAfter;
  }

  public RefactoringInfo setNameAfter(String nameAfter) {
    this.nameAfter = nameAfter;
    return this;
  }

}

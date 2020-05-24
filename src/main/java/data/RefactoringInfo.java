package data;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.refactoringminer.api.RefactoringType;

public class RefactoringInfo {

  @Nullable
  String elementBefore;
  @Nullable
  String elementAfter;
  private transient RefactoringEntry entry;
  private String text;
  private String name;
  private String nameBefore;
  private String nameAfter;
  private RefactoringType type;
  private List<TrueCodeRange> leftSide;
  private List<TrueCodeRange> rightSide;
  private Scope object;

  public RefactoringInfo(Scope object) {
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
    if (object == Scope.CLASS && nameBefore != null && nameAfter != null) {
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

    if (object == Scope.METHOD) {
      List<RefactoringInfo> refs = map.getOrDefault(nameBefore, new LinkedList<>());
      map.remove(nameBefore);
      refs.add(0, this);
      map.put(nameAfter, refs);
    }
  }

  @NotNull
  protected DefaultMutableTreeNode getSimpleNode() {
    DefaultMutableTreeNode change;
    if (nameAfter == null && nameBefore == null) {
      change = new DefaultMutableTreeNode("view changes");
    } else {
      char a = '→';
      change = new DefaultMutableTreeNode(nameBefore + " " + a + " " + nameAfter);
    }
    return change;
  }

  protected void getNodeClass(DefaultMutableTreeNode refName) {
    String before = nameBefore.substring(nameBefore.lastIndexOf(".") + 1);
    String after = nameAfter.substring(nameAfter.lastIndexOf(".") + 1);
    DefaultMutableTreeNode className;
    if (before.equals(after)) {
      className = new DefaultMutableTreeNode(after);
    } else {
      char a = '→';
      className = new DefaultMutableTreeNode(before + " " + a + " " + after);
    }

    String packageBefore = nameBefore.substring(0, nameBefore.lastIndexOf("."));
    String packageAfter = nameAfter.substring(0, nameAfter.lastIndexOf("."));
    DefaultMutableTreeNode package1 = new DefaultMutableTreeNode("before: " + packageBefore);
    DefaultMutableTreeNode package2 = new DefaultMutableTreeNode("after: " + packageAfter);
    className.add(package1);
    className.add(package2);
    refName.add(className);
  }

  @NotNull
  protected DefaultMutableTreeNode getNodeMethod() {
    DefaultMutableTreeNode change;
    final String elementBefore = this.elementBefore;
    final String elementAfter = this.elementAfter;
    if (elementAfter != null && elementBefore != null) {
      char a = '→';
      change = new DefaultMutableTreeNode(elementBefore + " " + a + " " + elementAfter);
    } else if (elementAfter != null) {
      change = new DefaultMutableTreeNode(elementAfter);
    } else if (elementBefore != null) {
      change = new DefaultMutableTreeNode(elementBefore);
    } else {
      change = new DefaultMutableTreeNode("view changes");
    }
    return change;
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

  public RefactoringEntry getEntry() {
    return entry;
  }

  public RefactoringInfo setEntry(RefactoringEntry entry) {
    this.entry = entry;
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

  public long getTimestamp() {
    return entry.getTimeStamp();
  }

  public String getCommitId() {
    return entry.getCommitId();
  }

  public Scope getObject() {
    return object;
  }

  public RefactoringInfo setElementBefore(@Nullable String elementBefore) {
    this.elementBefore = elementBefore;
    return this;
  }

  public RefactoringInfo setElementAfter(@Nullable String elementAfter) {
    this.elementAfter = elementAfter;
    return this;
  }
}

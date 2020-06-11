package data;

import com.google.gson.Gson;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import data.diff.DiffRequestGenerator;
import data.diff.ThreeSidedDiffRequestGenerator;
import data.diff.TwoSidedDiffRequestGenerator;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.refactoringminer.api.RefactoringType;

public class RefactoringInfo {

  private DiffRequestGenerator requestGenerator = new TwoSidedDiffRequestGenerator();
  private transient RefactoringEntry entry;

  private String name;

  private String[][] uiStrings = new String[3][2];
  private String[] paths = new String[3];

  private Set<String> includes = new HashSet<>();

  private RefactoringType type;
  private Group group;
  private String groupId;

  private boolean hidden = false;
  private boolean threeSided = false;


  public SimpleDiffRequest generate(DiffContent[] contents) {
    return requestGenerator.generate(contents, this);
  }

  /**
   * Adds this refactoring to the method history map.
   * Note that it should be called in chronological order.
   *
   * @param map for method history
   */
  public void addToHistory(Map<String, List<RefactoringInfo>> map) {
    if (group == Group.CLASS) {
      Map<String, String> renames = new HashMap<>();
      renames.put(getNameBefore(), getNameAfter());
      renames.forEach((before, after) -> map.keySet().stream()
          .filter(x -> x.substring(0, x.lastIndexOf("."))
              .equals(before))
          .forEach(signature -> {
            String newKey = after + signature.substring(signature.lastIndexOf("."));
            map.put(newKey, map.getOrDefault(signature, new ArrayList<>()));
          }));
      return;
    }

    if (group == Group.METHOD) {
      List<RefactoringInfo> refs = map.getOrDefault(getNameBefore(), new LinkedList<>());
      map.remove(getNameBefore());
      refs.add(0, this);
      map.put(getNameAfter(), refs);
    }
  }

  public RefactoringInfo addMarking(CodeRange left, CodeRange right, boolean hasColumns) {
    return addMarking(left, null, right, RefactoringLine.VisualisationType.TWO, null,
        RefactoringLine.MarkingOption.NONE, hasColumns);
  }

  public RefactoringInfo addMarking(CodeRange left, CodeRange right,
                                    Consumer<RefactoringLine> offsetFunction,
                                    RefactoringLine.MarkingOption option,
                                    boolean hasColumns) {
    return addMarking(left, null, right, RefactoringLine.VisualisationType.TWO, offsetFunction,
        option, hasColumns);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting
   */
  public RefactoringInfo addMarking(CodeRange left, CodeRange mid, CodeRange right,
                                    RefactoringLine.VisualisationType type,
                                    Consumer<RefactoringLine> offsetFunction,
                                    RefactoringLine.MarkingOption option,
                                    boolean hasColumns) {

    requestGenerator.addMarking(left, mid, right, type, offsetFunction, option, hasColumns);
    setLeftPath(left.getFilePath());
    if (mid != null) {
      setMidPath(mid.getFilePath());
    }
    setRightPath(right.getFilePath());
    return this;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public void addAllMarkings(RefactoringInfo info) {
    requestGenerator.getMarkings().addAll(info.getLineMarkings());
  }

  public void addIncludedRefactoring(String refactoring) {
    this.includes.add(refactoring);
  }

  public Set<String> getIncludingRefactorings() {
    return includes;
  }

  public String getParent() {
    return entry.getParent();
  }

  public List<RefactoringLine> getLineMarkings() {
    return requestGenerator.getMarkings();
  }

  public long getTimestamp() {
    return entry.getTimeStamp();
  }

  public String getCommitId() {
    return entry.getCommitId();
  }

  public Group getGroup() {
    return group;
  }

  public RefactoringInfo setGroup(Group group) {
    this.group = group;
    return this;
  }

  public boolean isThreeSided() {
    return threeSided;
  }

  /**
   * Sets the refactoring info as three sided.
   *
   * @param threeSided boolean
   * @return this
   */
  public RefactoringInfo setThreeSided(boolean threeSided) {
    this.threeSided = threeSided;
    if (threeSided) {
      requestGenerator = new ThreeSidedDiffRequestGenerator();
    }
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

  public String getName() {
    return name;
  }

  public RefactoringInfo setName(String name) {
    this.name = name;
    return this;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public String getGroupId() {
    return groupId;
  }

  public RefactoringInfo setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public String getLeftPath() {
    return paths[0];
  }

  public RefactoringInfo setLeftPath(String leftPath) {
    paths[0] = leftPath;
    return this;
  }

  public String getMidPath() {
    return paths[1];
  }

  public RefactoringInfo setMidPath(String midPath) {
    paths[1] = midPath;
    return this;
  }

  public String getRightPath() {
    return paths[2];
  }

  public RefactoringInfo setRightPath(String rightPath) {
    paths[2] = rightPath;
    return this;
  }

  public String getNameBefore() {
    return uiStrings[0][0];
  }

  public RefactoringInfo setNameBefore(String nameBefore) {
    uiStrings[0][0] = nameBefore;
    return this;
  }

  public String getNameAfter() {
    return uiStrings[0][1];
  }

  public RefactoringInfo setNameAfter(String nameAfter) {
    uiStrings[0][1] = nameAfter;
    return this;
  }

  /**
   * Method for create a presentable String out of the
   * name refactoring.
   *
   * @return presentable String that shows the changes if existent, else shows a presentable name.
   */
  public String getDisplayableName() {
    String before = getNameBefore();
    if (before.contains(".")) {
      before = getNameBefore().substring(getNameBefore().lastIndexOf(".") + 1);
    }
    String after = getNameAfter();
    if (after.contains(".")) {
      after = getNameAfter().substring(getNameAfter().lastIndexOf(".") + 1);
    }
    if (before.equals(after)) {
      return before;
    } else {
      return before + " -> " + after;
    }
  }

  public String getElementBefore() {
    return uiStrings[1][0];
  }

  public RefactoringInfo setElementBefore(String elementBefore) {
    uiStrings[1][0] = elementBefore;
    return this;
  }

  public String getElementAfter() {
    return uiStrings[1][1];
  }

  public RefactoringInfo setElementAfter(String elementAfter) {
    uiStrings[1][1] = elementAfter;
    return this;
  }

  public String getDetailsBefore() {
    return uiStrings[2][0];
  }

  public RefactoringInfo setDetailsBefore(String detailsBefore) {
    uiStrings[2][0] = detailsBefore;
    return this;
  }

  public String getDetailsAfter() {
    return uiStrings[2][1];
  }

  public RefactoringInfo setDetailsAfter(String detailsAfter) {
    uiStrings[2][1] = detailsAfter;
    return this;
  }

  public void correctLines(String before, String mid, String after) {
    requestGenerator.correct(before, mid, after);
  }

  public enum Group {
    METHOD,
    CLASS,
    ATTRIBUTE,
    VARIABLE,
    INTERFACE,
    ABSTRACT,
    PACKAGE
  }
}

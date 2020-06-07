package data;

import static ui.windows.DiffWindow.REFACTORING_INFO;

import com.google.gson.Gson;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.util.DiffUserDataKeysEx;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.Nullable;
import org.refactoringminer.api.RefactoringType;

public class RefactoringInfo {

  @Nullable
  private String elementBefore;
  @Nullable
  private String elementAfter;

  private String name;
  private String nameBefore;
  private String nameAfter;
  private Set<String> includes = new HashSet<>();

  private transient RefactoringEntry entry;
  private String leftPath;
  private String midPath;
  private String rightPath;
  private RefactoringType type;
  private final List<RefactoringLine> lineMarkings = new ArrayList<>();
  private Group group;
  private boolean threeSided = false;
  private String groupId;
  private boolean hidden = false;


  /**
   * Adds this refactoring to the method history map.
   * Note that it should be called in chronological order.
   *
   * @param map for method history
   */
  public void addToHistory(Map<String, List<RefactoringInfo>> map) {
    if (group == Group.CLASS) {
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

    if (group == Group.METHOD) {
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

  public List<RefactoringLine> getLineMarkings() {
    return lineMarkings;
  }

  public void addAllMarkings(RefactoringInfo info) {
    this.lineMarkings.addAll(info.getLineMarkings());
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

  public void includesRefactoring(String refactoring) {
    this.includes.add(refactoring);
  }

  public Set<String> getIncludingRefactorings() {
    return includes;
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

    RefactoringLine line = new RefactoringLine(left, mid, right, type, option, hasColumns);
    if (offsetFunction != null) {
      offsetFunction.accept(line);
    }
    lineMarkings.add(line);
    this.leftPath = left.getFilePath();
    if (mid != null) {
      this.midPath = mid.getFilePath();
    }
    this.rightPath = right.getFilePath();
    return this;
  }

  /**
   * Get line markings for two sided window.
   * Should only be called if isThreeSided() evaluates to false.
   */
  public List<LineFragment> getTwoSidedLineMarkings(String leftText, String rightText) {
    assert !threeSided;
    return lineMarkings.stream().map(l ->
        l.getTwoSidedRange(leftText, rightText)).collect(Collectors.toList());
  }

  /**
   * Get line markings for two sided window.
   * Should only be called if isThreeSided() evaluates to true.
   */
  public List<SimpleThreesideDiffChange> getThreeSidedLineMarkings(SimpleThreesideDiffViewer
                                                                       viewer) {
    assert threeSided;
    return lineMarkings.stream()
        .map(line -> line.getThreeSidedRange(viewer))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public RefactoringInfo setNameBefore(String nameBefore) {
    this.nameBefore = nameBefore;
    return this;
  }

  public Group getGroup() {
    return group;
  }

  public RefactoringInfo setGroup(Group group) {
    this.group = group;
    return this;
  }

  public String getLeftPath() {
    return leftPath;
  }

  public String getMidPath() {
    return midPath;
  }

  public String getRightPath() {
    return rightPath;
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

  public boolean isThreeSided() {
    return threeSided;
  }

  public RefactoringInfo setThreeSided(boolean threeSided) {
    this.threeSided = threeSided;
    return this;
  }

  public RefactoringInfo setElementBefore(@Nullable String elementBefore) {
    this.elementBefore = elementBefore;
    return this;
  }

  public RefactoringInfo setElementAfter(@Nullable String elementAfter) {
    this.elementAfter = elementAfter;
    return this;
  }

  /**
   * Creates a DefaultMutableTreeNode for the UI.
   *
   * @return the node.
   */
  public DefaultMutableTreeNode makeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
    DefaultMutableTreeNode child = new DefaultMutableTreeNode(getDisplayableName());
    node.add(child);
    addLeaves(child);
    return node;
  }

  /**
   * Adds leaves for refactorings where the element
   * is not null.
   *
   * @param node to add leaves to.
   */
  public void addLeaves(DefaultMutableTreeNode node) {
    String displayableElement = getDisplayableElement();
    if (displayableElement == null) {
      return;
    }
    node.add(new DefaultMutableTreeNode(displayableElement));
  }

  /**
   * Method for create a presentable String out of the
   * element changes for a refactoring.
   *
   * @return presentable String that shows the changes.
   */
  public String getDisplayableElement() {
    if (elementBefore == null) {
      return null;
    }
    String info = elementBefore;
    if (elementAfter != null) {
      info += " -> " + elementAfter;
    }
    return info;
  }

  /**
   * Creates a diff request for this refactoring.
   *
   * @param contents array of diffcontents
   * @return the diff request created
   */
  public SimpleDiffRequest createDiffRequest(DiffContent[] contents) {
    SimpleDiffRequest request;
    if (!threeSided) {
      request = new SimpleDiffRequest(name,
          contents[0], contents[2], leftPath, rightPath);
      request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
          (text1, text2, policy, innerChanges, indicator)
              -> getTwoSidedLineMarkings(text1.toString(), text2.toString()));
    } else {
      request = new SimpleDiffRequest(getName(),
          contents[0], contents[1], contents[2],
          leftPath, midPath, rightPath);
      request.putUserData(REFACTORING_INFO, this);
    }
    return request;
  }

  /**
   * Method for create a presentable String out of the
   * name refactoring.
   *
   * @return presentable String that shows the changes if existent, else shows a presentable name.
   */
  public String getDisplayableName() {
    String before = nameBefore;
    if (before.contains(".")) {
      before = nameBefore.substring(nameBefore.lastIndexOf(".") + 1);
    }
    String after = nameAfter;
    if (after.contains(".")) {
      after = nameAfter.substring(nameAfter.lastIndexOf(".") + 1);
    }
    if (before.equals(after)) {
      return before;
    } else {
      return before + "-> " + after;
    }
  }
}

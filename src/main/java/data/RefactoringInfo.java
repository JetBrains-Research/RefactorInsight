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
import org.refactoringminer.api.RefactoringType;
import utils.Utils;

public class RefactoringInfo {

  private final List<RefactoringLine> lineMarkings = new ArrayList<>();

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

    RefactoringLine line = new RefactoringLine(left, mid, right, type, option, hasColumns);
    if (offsetFunction != null) {
      offsetFunction.accept(line);
    }
    lineMarkings.add(line);
    setLeftPath(left.getFilePath());
    if (mid != null) {
      setMidPath(mid.getFilePath());
    }
    setRightPath(right.getFilePath());
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

  /**
   * Creates a DefaultMutableTreeNode for the UI.
   *
   * @return the node.
   */
  public DefaultMutableTreeNode makeNode() {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
    DefaultMutableTreeNode root = makeDetailsNode(node);
    makeNameNode(root);
    return node;
  }

  /**
   * Creates a node based on the nameBefore & nameAfter attributes.
   *
   * @param root to add node to.
   */
  public void makeNameNode(DefaultMutableTreeNode root) {
    DefaultMutableTreeNode child = new DefaultMutableTreeNode(
        group == Group.METHOD
            ? getDisplayableName()
            : Utils.getDisplayableDetails(getNameBefore(), getNameAfter()));
    root.add(child);
    addLeaves(child);
  }

  /**
   * Creates a node iff the detailsBefore & detailsAfter attributes are not null.
   *
   * @param root current root of the tree.
   * @return the same root if no node was created.
   */
  private DefaultMutableTreeNode makeDetailsNode(DefaultMutableTreeNode root) {
    if (Utils.getDisplayableDetails(getDetailsBefore(), getDetailsAfter()) != null) {
      DefaultMutableTreeNode details =
          new DefaultMutableTreeNode(
              Utils.getDisplayableDetails(getDetailsBefore(), getDetailsAfter()));
      root.add(details);
      return details;
    }
    return root;
  }

  /**
   * Adds leaves for refactorings where the element
   * is not null.
   *
   * @param node to add leaves to.
   */
  public void addLeaves(DefaultMutableTreeNode node) {
    String displayableElement = Utils.getDisplayableElement(getElementBefore(), getElementAfter());
    if (displayableElement == null) {
      return;
    }
    node.add(new DefaultMutableTreeNode(displayableElement));
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
          contents[0], contents[2], getLeftPath(), getRightPath());
      request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
          (text1, text2, policy, innerChanges, indicator)
              -> getTwoSidedLineMarkings(text1.toString(), text2.toString()));
    } else {
      request = new SimpleDiffRequest(getName(),
          contents[0], contents[1], contents[2],
          getLeftPath(), getMidPath(), getRightPath());
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
      return before + "-> " + after;
    }
  }


  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public void addAllMarkings(RefactoringInfo info) {
    this.lineMarkings.addAll(info.getLineMarkings());
  }

  public void addIncludedRefactoring(String refactoring) {
    this.includes.add(refactoring);
  }

  public Set<String> getIncludingRefactorings() {
    return includes;
  }

  public List<RefactoringLine> getLineMarkings() {
    return lineMarkings;
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

  public RefactoringInfo setThreeSided(boolean threeSided) {
    this.threeSided = threeSided;
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
}

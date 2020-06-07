package data;

import com.google.gson.Gson;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
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
import utils.Utils;

public class RefactoringInfo {

  private final List<RefactoringLine> lineMarkings = new ArrayList<>();
  @Nullable
  private String elementBefore;
  @Nullable
  private String elementAfter;
  private String name;
  private String nameBefore;
  private String nameAfter;
  @Nullable
  private String detailsBefore;
  @Nullable
  private String detailsAfter;

  private Set<String> includes = new HashSet<>();
  private transient RefactoringEntry entry;
  private String leftPath;
  private String midPath;
  private String rightPath;
  private RefactoringType type;
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

  public void includesRefactoring(String refactoring) {
    this.includes.add(refactoring);
  }

  public Set<String> getIncludingRefactorings() {
    return includes;
  }

  public RefactoringInfo addMarking(CodeRange left, CodeRange right) {
    return addMarking(left, null, right, RefactoringLine.VisualisationType.TWO, null);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   *
   * @param startBefore int
   * @param endBefore   int
   * @param startAfter  int
   * @param endAfter    int
   * @param beforePath  String
   * @param afterPath   String
   * @return this
   */
  public RefactoringInfo addMarking(int startBefore, int endBefore,
                                    int startAfter, int endAfter,
                                    String beforePath, String afterPath) {
    return addMarking(startBefore, endBefore, 0, 0, startAfter, endAfter, beforePath, "", afterPath,
        RefactoringLine.VisualisationType.TWO, null);
  }

  public RefactoringInfo addMarking(CodeRange left, CodeRange right,
                                    Consumer<RefactoringLine> offsetFunction) {
    return addMarking(left, null, right, RefactoringLine.VisualisationType.TWO, offsetFunction);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting
   *
   * @param startBefore    int
   * @param endBefore      int
   * @param startAfter     int
   * @param endAfter       int
   * @param beforePath     String
   * @param afterPath      String
   * @param offsetFunction Consumer for adding subhighlightings
   * @return
   */
  public RefactoringInfo addMarking(int startBefore, int endBefore,
                                    int startAfter, int endAfter,
                                    String beforePath, String afterPath,
                                    Consumer<RefactoringLine> offsetFunction) {
    return addMarking(startBefore, endBefore, 0, 0, startAfter, endAfter, beforePath, "", afterPath,
        RefactoringLine.VisualisationType.TWO, offsetFunction);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   */
  public RefactoringInfo addMarking(CodeRange left, CodeRange mid, CodeRange right,
                                    RefactoringLine.VisualisationType type) {

    return addMarking(left, mid, right, type, null);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting
   */
  public RefactoringInfo addMarking(int startLeft, int endLeft,
                                    int startMid, int endMid,
                                    int startRight, int endRight,
                                    String leftPath, String midPath, String rightPath,
                                    RefactoringLine.VisualisationType type) {
    return addMarking(startLeft, endLeft, startMid, endMid, startRight, endRight, leftPath, midPath,
        rightPath, type, null);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting
   */
  public RefactoringInfo addMarking(CodeRange left, CodeRange mid, CodeRange right,
                                    RefactoringLine.VisualisationType type,
                                    Consumer<RefactoringLine> offsetFunction) {

    RefactoringLine line = new RefactoringLine(left, mid, right, type);
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
   * Add line marking for diffwindow used to display refactorings.
   */
  public RefactoringInfo addMarking(int startLeft, int endLeft,
                                    int startMid, int endMid,
                                    int startRight, int endRight,
                                    String leftPath, String midPath, String rightPath,
                                    RefactoringLine.VisualisationType type,
                                    Consumer<RefactoringLine> offsetFunction) {

    RefactoringLine line =
        new RefactoringLine(startLeft - 1, endLeft, startMid - 1, endMid, startRight - 1, endRight,
            type);
    if (offsetFunction != null) {
      offsetFunction.accept(line);
    }
    lineMarkings.add(line);
    this.leftPath = leftPath;
    this.midPath = midPath;
    this.rightPath = rightPath;
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
  public List<SimpleThreesideDiffChange> getThreeSidedLineMarkings(String textLeft,
                                                                   String textMid,
                                                                   String textRight,
                                                                   SimpleThreesideDiffViewer
                                                                       viewer) {
    assert threeSided;
    return lineMarkings.stream()
        .map(line -> line.getThreeSidedRange(textLeft, textMid, textRight, viewer))
        .collect(Collectors.toList());
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

  public List<String> getParents() {
    return entry.getParents();
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
   * @param root to add node to.
   */
  public void makeNameNode(DefaultMutableTreeNode root) {
    DefaultMutableTreeNode child = new DefaultMutableTreeNode(
        group == Group.METHOD
            ? getDisplayableName()
            : Utils.getDisplayableDetails(nameBefore, nameAfter));
    root.add(child);
    addLeaves(child);
  }

  /**
   * Creates a node iff the detailsBefore & detailsAfter attributes are not null.
   * @param root current root of the tree.
   * @return the same root if no node was created.
   */
  private DefaultMutableTreeNode makeDetailsNode(DefaultMutableTreeNode root) {
    if (Utils.getDisplayableDetails(detailsBefore, detailsAfter) != null) {
      DefaultMutableTreeNode details =
          new DefaultMutableTreeNode(Utils.getDisplayableDetails(detailsBefore, detailsAfter));
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
    String displayableElement = Utils.getDisplayableElement(elementBefore, elementAfter);
    if (displayableElement == null) {
      return;
    }
    node.add(new DefaultMutableTreeNode(displayableElement));
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

  @Nullable
  public String getElementBefore() {
    return elementBefore;
  }

  public RefactoringInfo setElementBefore(@Nullable String elementBefore) {
    this.elementBefore = elementBefore;
    return this;
  }

  @Nullable
  public String getElementAfter() {
    return elementAfter;
  }

  public RefactoringInfo setElementAfter(@Nullable String elementAfter) {
    this.elementAfter = elementAfter;
    return this;
  }

  @Nullable
  public String getDetailsBefore() {
    return detailsBefore;
  }

  public RefactoringInfo setDetailsBefore(String detailsBefore) {
    this.detailsBefore = detailsBefore;
    return this;
  }

  @Nullable
  public String getDetailsAfter() {
    return detailsAfter;
  }

  public RefactoringInfo setDetailsAfter(String detailsAfter) {
    this.detailsAfter = detailsAfter;
    return this;
  }
}

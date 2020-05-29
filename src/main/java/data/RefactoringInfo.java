package data;

import com.google.gson.Gson;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jetbrains.annotations.Nullable;
import org.refactoringminer.api.RefactoringType;

public class RefactoringInfo {

  @Nullable
  String elementBefore;
  @Nullable
  String elementAfter;
  private transient RefactoringEntry entry;
  private String name;
  private String nameBefore;
  private String nameAfter;
  private String leftPath;
  private String midPath;
  private String rightPath;
  private RefactoringType type;
  private List<RefactoringLine> lineMarkings = new ArrayList<>();
  private Group group;
  private boolean threeSided = false;

  /**
   * Adds this refactoring to the method history map.
   * Note that it should be called in chronological order.
   *
   * @param map for method history
   */
  public void addToHistory(Map<String, List<RefactoringInfo>> map) {
    if (group == Group.CLASS && nameBefore != null && nameAfter != null) {
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

  public RefactoringInfo addMarking(CodeRange left, CodeRange right) {
    return addMarking(left.getStartLine(), left.getEndLine(), right.getStartLine(),
        right.getEndLine(), left.getFilePath(), right.getFilePath());
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
        RefactoringLine.ThreeSidedType.BOTH, null);
  }

  public RefactoringInfo addMarking(CodeRange left, CodeRange right,
                                    Consumer<RefactoringLine> offsetFunction) {
    return addMarking(left.getStartLine(), left.getEndLine(), right.getStartLine(),
        right.getEndLine(), left.getFilePath(), right.getFilePath(), offsetFunction);
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
        RefactoringLine.ThreeSidedType.BOTH, offsetFunction);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   */
  public RefactoringInfo addMarking(CodeRange left, CodeRange mid, CodeRange right,
                                    RefactoringLine.ThreeSidedType type) {

    return addMarking(left.getStartLine(), left.getEndLine(),
        mid.getStartLine(), mid.getEndLine(),
        right.getStartLine(), right.getEndLine(),
        left.getFilePath(), mid.getFilePath(), right.getFilePath(), type);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting
   */
  public RefactoringInfo addMarking(int startLeft, int endLeft,
                                    int startMid, int endMid,
                                    int startRight, int endRight,
                                    String leftPath, String midPath, String rightPath,
                                    RefactoringLine.ThreeSidedType type) {
    return addMarking(startLeft, endLeft, startMid, endMid, startRight, endRight, leftPath, midPath,
        rightPath, type, null);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting
   */
  public RefactoringInfo addMarking(CodeRange left, CodeRange mid, CodeRange right,
                                    RefactoringLine.ThreeSidedType type,
                                    Consumer<RefactoringLine> offsetFunction) {

    return addMarking(left.getStartLine(), left.getEndLine(),
        mid.getStartLine(), mid.getEndLine(),
        right.getStartLine(), right.getEndLine(),
        left.getFilePath(), mid.getFilePath(), right.getFilePath(), type, offsetFunction);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   */
  public RefactoringInfo addMarking(int startLeft, int endLeft,
                                    int startMid, int endMid,
                                    int startRight, int endRight,
                                    String leftPath, String midPath, String rightPath,
                                    RefactoringLine.ThreeSidedType type,
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
  public List<LineFragment> getTwoSidedLineMarkings(int maxLineBefore, int maxLineAfter) {
    return lineMarkings.stream().map(l ->
        l.getTwoSidedRange(maxLineBefore, maxLineAfter)).collect(Collectors.toList());
  }

  /**
   * Get line markings for two sided window.
   * Should only be called if isThreeSided() evaluates to true.
   */
  public List<SimpleThreesideDiffChange> getThreeSidedLineMarkings(int maxLineLeft,
                                                                   int maxLineMid,
                                                                   int maxLineRight,
                                                                   SimpleThreesideDiffViewer
                                                                       viewer) {
    return lineMarkings.stream()
        .map(line -> line.getThreeSidedRange(maxLineLeft, maxLineMid, maxLineRight, viewer))
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

package data;

import com.google.gson.Gson;
import com.intellij.diff.fragments.LineFragment;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.refactoringminer.api.RefactoringType;

public class RefactoringInfo {

  private String text;
  private String name;
  private String commitId;
  private String nameBefore;
  private String nameAfter;
  private String beforePath;
  private String afterPath;
  private RefactoringType type;
  private List<RefactoringLine> lineMarkings = new ArrayList<>();
  private Group group;

  public RefactoringInfo setGroup(Group group) {
    this.group = group;
    return this;
  }

  /**
   * Adds this refactoring to the method history map.
   * Note that it should be called in chronological order.
   *
   * @param map for method history
   */
  public void addToHistory(Map<String, List<RefactoringInfo>> map) {
    //System.out.println(signatureAfter);
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
   * @param beforePath  int
   * @param afterPath   int
   * @return this
   */
  public RefactoringInfo addMarking(int startBefore, int endBefore, int startAfter, int endAfter,
                                    String beforePath, String afterPath) {
    lineMarkings
        .add(
            new RefactoringLine(startBefore - 1, endBefore, startAfter - 1, endAfter));
    this.beforePath = beforePath;
    this.afterPath = afterPath;
    return this;
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
   * @param beforePath     int
   * @param afterPath      int
   * @param offsetFunction Consumer for adding subhighlightings
   * @return
   */
  public RefactoringInfo addMarking(int startBefore, int endBefore, int startAfter, int endAfter,
                                    String beforePath, String afterPath,
                                    Consumer<RefactoringLine> offsetFunction) {
    RefactoringLine line =
        new RefactoringLine(startBefore - 1, endBefore, startAfter - 1, endAfter);
    offsetFunction.accept(line);
    lineMarkings.add(line);
    this.beforePath = beforePath;
    this.afterPath = afterPath;
    return this;
  }

  public List<LineFragment> getLineMarkings() {
    return lineMarkings.stream().map(RefactoringLine::toLineFragment).collect(Collectors.toList());
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

  public String getBeforePath() {
    return beforePath;
  }

  public String getAfterPath() {
    return afterPath;
  }

  public String getNameAfter() {
    return nameAfter;
  }

  public RefactoringInfo setNameAfter(String nameAfter) {
    this.nameAfter = nameAfter;
    return this;
  }

}

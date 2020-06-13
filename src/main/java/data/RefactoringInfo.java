package data;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import data.diffRequests.DiffRequestGenerator;
import data.diffRequests.ThreeSidedDiffRequestGenerator;
import data.diffRequests.TwoSidedDiffRequestGenerator;
import gr.uom.java.xmi.diff.CodeRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.refactoringminer.api.RefactoringType;
import utils.Utils;

public class RefactoringInfo {

  private transient RefactoringEntry entry;
  private transient String groupId;
  private transient RefactoringType type;

  private DiffRequestGenerator requestGenerator = new TwoSidedDiffRequestGenerator();
  private String name;

  private String[][] uiStrings = new String[3][2];
  private String[] paths = new String[3];

  private Group group;

  private Set<String> includes = new HashSet<>();

  private boolean hidden = false;
  private boolean threeSided = false;


  public SimpleDiffRequest generate(DiffContent[] contents) {
    return requestGenerator.generate(contents, this);
  }

  public String toString() {
    return String.join(Utils.INFO_DELIMITER,
        name,
        Stream.concat(
            Arrays.stream(uiStrings).flatMap(Arrays::stream),
            Arrays.stream(paths))
            .map(s -> s == null ? "null" : s)
            .collect(Collectors.joining(Utils.INFO_DELIMITER)),
        group.toString(),
        threeSided ? "t" : "f",
        hidden ? "t" : "f",
        requestGenerator.toString(),
        String.join(Utils.INFO_DELIMITER, includes)
    );
  }

  public static RefactoringInfo fromString(String value) {
    String[] tokens = value.split(Utils.INFO_DELIMITER, 15);
    return new RefactoringInfo()
        .setName(tokens[0])
        .setNameBefore(tokens[1])
        .setElementBefore(tokens[2])
        .setDetailsBefore(tokens[3])
        .setNameAfter(tokens[4])
        .setElementAfter(tokens[5])
        .setDetailsAfter(tokens[6])
        .setLeftPath(tokens[7])
        .setMidPath(tokens[8])
        .setRightPath(tokens[9])
        .setGroup(Group.valueOf(tokens[10]))
        .setThreeSided(tokens[11].equals("t"))
        .setHidden(tokens[12].equals("t"))
        .setRequestGenerator(tokens[11].equals("t")
            ? ThreeSidedDiffRequestGenerator.fromString(tokens[13])
            : TwoSidedDiffRequestGenerator.fromString(tokens[13]))
        .setIncludes(new HashSet<>(Arrays.asList(tokens[14].split(Utils.INFO_DELIMITER))));
  }

  public RefactoringInfo setRequestGenerator(DiffRequestGenerator requestGenerator) {
    this.requestGenerator = requestGenerator;
    return this;
  }

  public RefactoringInfo setIncludes(Set<String> includes) {
    this.includes = includes;
    return this;
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

  public RefactoringInfo setHidden(boolean hidden) {
    this.hidden = hidden;
    return this;
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

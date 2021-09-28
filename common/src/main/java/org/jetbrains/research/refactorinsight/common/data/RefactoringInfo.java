package org.jetbrains.research.refactorinsight.common.data;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.research.refactorinsight.common.diff.*;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.utils.Utils;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;
import org.jetbrains.research.refactorinsight.common.utils.StringUtils;

/**
 * Stores the information needed for displaying one refactoring:
 * diff request generator, name, ui node strings, paths, the group where it belongs,
 * if it is hidden, three-sided or a more-sided refactoring.
 * Contains also information about any refactoring that it implies (used when combining refactorings).
 * Here, each refactoring is added to the refactoring history map used in `Show Refactoring History` action.
 */
public class RefactoringInfo {

  private transient RefactoringEntry entry;
  private transient String groupId;
  private transient List<Pair<String, Boolean>> moreSidedLeftPaths = new ArrayList<>();

  private DiffRequestGenerator requestGenerator = new TwoSidedDiffRequestGenerator();

  private final String[][] uiStrings = new String[3][2];
  private final String[] paths = new String[3];
  // Optional data for foldable refactorings
  private final FoldingDescriptor[] foldingPositions = new FoldingDescriptor[3];

  private RefactoringType type;
  private Group group;

  private Set<String> includes = new HashSet<>();

  private boolean hidden = false;
  private boolean threeSided = false;
  private boolean moreSided = false;

  // Optional data for move refactorings
  private boolean changed = true;

/*
TODO
   * Deserializes an {@link RefactoringInfo} instance from string.
   *
   * @param value string.
   * @return the RefactoringInfo.
   *//*
  public static RefactoringInfo fromString(String value) {
    String regex = StringUtils.delimiter(StringUtils.INFO, true);
    String[] tokens = value.split(regex, 20);
    RefactoringInfo info = new RefactoringInfo()
        .setType(RefactoringType.values[Integer.parseInt(tokens[0])])
        .setNameBefore(StringUtils.deSanitize(tokens[1]))
        .setNameAfter(StringUtils.deSanitize(tokens[2]))
        .setElementBefore(StringUtils.deSanitize(tokens[3]))
        .setElementAfter(StringUtils.deSanitize(tokens[4]))
        .setDetailsBefore(StringUtils.deSanitize(tokens[5]))
        .setDetailsAfter(StringUtils.deSanitize(tokens[6]))
        .setLeftPath(StringUtils.deSanitize(tokens[7]))
        .setMidPath(StringUtils.deSanitize(tokens[8]))
        .setRightPath(StringUtils.deSanitize(tokens[9]))
        .setGroup(Group.values[Integer.parseInt(tokens[10])])
        .setThreeSided(tokens[11].equals("t"))
        .setHidden(tokens[12].equals("t"))
        .setMoreSided(tokens[13].equals("t"))
        .setChanged(tokens[14].equals("t"))
        .setFoldingDescriptorBefore(FoldingDescriptor.fromString(tokens[16]))
        .setFoldingDescriptorMid(FoldingDescriptor.fromString(tokens[17]))
        .setFoldingDescriptorAfter(FoldingDescriptor.fromString(tokens[18]))
        .setIncludes(new HashSet<>(
            tokens[19].isEmpty() ? List.of() : Arrays.asList(tokens[19].split(regex))));

    DiffRequestGenerator diffGenerator;
    if (info.isMoreSided()) {
      diffGenerator = MoreSidedDiffRequestGenerator.fromString(tokens[15]);
    } else if (info.isThreeSided()) {
      diffGenerator = ThreeSidedDiffRequestGenerator.fromString(tokens[15]);
    } else {
      diffGenerator = TwoSidedDiffRequestGenerator.fromString(tokens[15]);
    }

    return info.setRequestGenerator(diffGenerator);
  }*/

  public SimpleDiffRequest generate(DiffContent[] contents) {
    return requestGenerator.generate(contents, this);
  }

  /**
   * Serializes an {@link RefactoringInfo} instance to string.
   *
   * @return string value.
   */
  public String toString() {
    return String.join(StringUtils.delimiter(StringUtils.INFO),
        String.valueOf(type.ordinal()),
        Stream.concat(
            Arrays.stream(uiStrings).flatMap(Arrays::stream),
            Arrays.stream(paths))
            .map(s -> s == null ? "" : s)
            .map(StringUtils::sanitize)
            .collect(Collectors.joining(StringUtils.delimiter(StringUtils.INFO))),
        String.valueOf(group.ordinal()),
        threeSided ? "t" : "",
        hidden ? "t" : "",
        moreSided ? "t" : "",
        changed ? "t" : "",
        requestGenerator.toString(),
        Arrays.stream(foldingPositions)
            .map(fp -> fp == null ? "" : fp.toString())
            .collect(Collectors.joining(StringUtils.delimiter(StringUtils.INFO))),
        String.join(StringUtils.delimiter(StringUtils.INFO), includes)
    );
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
   * Adds the refactoring to the method history map.
   * Note that it should be called in chronological order.
   *
   * @param map for method history.
   */
  public void addToHistory(Map<String, Set<RefactoringInfo>> map) {
    changeKeys(map);
    String before = getNameBefore();
    String after = getNameAfter();
    if (group == Group.ATTRIBUTE) {
      before = getDetailsBefore() + "|" + getNameBefore();
      after = getDetailsAfter() + "|" + getNameAfter();
    }

    if (group != Group.VARIABLE) {
      Set<RefactoringInfo> data = map.getOrDefault(before, new HashSet<>());
      map.remove(before);
      Set<RefactoringInfo> data2 = map.getOrDefault(after, new HashSet<>());
      data.add(this);
      data2.addAll(data);
      map.put(after, data2);
      if (moreSided) {
        ((MoreSidedDiffRequestGenerator) requestGenerator).getClassNames()
            .forEach(name -> {
              Set<RefactoringInfo> infos = map.getOrDefault(name, new HashSet<>());
              RefactoringInfo info = new RefactoringInfo()
                  .setGroup(group)
                  .setNameBefore(getNameBefore())
                  .setNameAfter(getNameAfter())
                  .setType(type)
                  .setIncludes(includes)
                  .setHidden(hidden)
                  .setRequestGenerator(requestGenerator)
                  .setLeftPath(getLeftPath())
                  .setRightPath(getRightPath())
                  .setMidPath(getMidPath())
                  .setMoreSided(moreSided)
                  .setThreeSided(threeSided)
                  .setEntry(entry)
                  .setElementBefore(getNameAfter().substring(getNameAfter().lastIndexOf('.') + 1));
              infos.add(info);
              map.put(name, infos);
            });
      }
    }

  }

  private void changeKeys(Map<String, Set<RefactoringInfo>> map) {
    if ((group == Group.CLASS || group == Group.ABSTRACT || group == Group.INTERFACE)
        && !getNameBefore().equals(getNameAfter())) {
      changeAttributesSignature(map);
      changeMethodsSignature(map);
    }
  }

  private void changeMethodsSignature(Map<String, Set<RefactoringInfo>> map) {
    map.keySet().stream()
        .filter(x -> !x.contains("|"))
        .filter(x -> x.contains(".")).filter(x -> x.substring(0, x.lastIndexOf("."))
        .equals(getNameBefore()))
        .forEach(signature -> {
          String methodName = signature.substring(signature.lastIndexOf(".") + 1);
          if (methodName.contains("(")) {
            methodName = methodName.substring(0, methodName.indexOf("("));
          }
          String newKey;
          //change constructor name in case of a class rename
          if (methodName.equals(getNameBefore().substring(getNameBefore().lastIndexOf(".") + 1))) {
            newKey =
                getNameAfter() + "." + getNameAfter().substring(getNameAfter().lastIndexOf(".") + 1)
                    + signature.substring(signature.indexOf("("));
          } else {
            newKey = getNameAfter() + signature.substring(signature.lastIndexOf("."));
          }
          map.put(newKey, map.getOrDefault(signature, new HashSet<>()));
          map.remove(signature);
        });
  }

  private void changeAttributesSignature(Map<String, Set<RefactoringInfo>> map) {
    map.keySet().stream()
        .filter(x -> x.contains("|")).filter(x -> x.substring(0, x.lastIndexOf("|"))
        .equals(getNameBefore()))
        .forEach(signature -> {
          String newKey = getNameAfter() + signature.substring(signature.lastIndexOf("|"));
          map.put(newKey, map.getOrDefault(signature, new HashSet<>()));
          map.remove(signature);
        });
  }

  public RefactoringInfo addMarking(CodeRange left, CodeRange right, boolean hasColumns) {
    return addMarking(left, null, right, VisualizationType.TWO, null,
                      RefactoringLine.MarkingOption.NONE, hasColumns);
  }

  public RefactoringInfo addMarking(CodeRange left, CodeRange right,
                                    Consumer<RefactoringLine> offsetFunction,
                                    RefactoringLine.MarkingOption option,
                                    boolean hasColumns) {
    return addMarking(left, null, right, VisualizationType.TWO, offsetFunction,
                      option, hasColumns);
  }

  /**
   * Add line marking for diffwindow used to display refactorings.
   * Includes possibility for sub-highlighting.
   */
  public RefactoringInfo addMarking(CodeRange left, CodeRange mid, CodeRange right,
                                    VisualizationType type,
                                    Consumer<RefactoringLine> offsetFunction,
                                    RefactoringLine.MarkingOption option,
                                    boolean hasColumns) {

    requestGenerator.addMarking(left, mid, right, type, offsetFunction, option, hasColumns);
    if (left != null) {
      setLeftPath(left.getFilePath());
      if (moreSided) {
        moreSidedLeftPaths.add(new Pair<>(left.getFilePath(), true));
      }
    }
    if (mid != null) {
      setMidPath(mid.getFilePath());
    }
    if (right != null) {
      setRightPath(right.getFilePath());
    }
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

  public boolean containsElement(int lineNumber, int textOffset, boolean isRight) {
    return requestGenerator.containsElement(lineNumber, textOffset, isRight);
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

  public boolean isMoreSided() {
    return moreSided;
  }

  /**
   * Sets boolean for more sided refactoring types (Extract method).
   *
   * @param moreSided Boolean
   * @return this
   */
  public RefactoringInfo setMoreSided(boolean moreSided) {
    this.moreSided = moreSided;
    if (moreSided) {
      requestGenerator = new MoreSidedDiffRequestGenerator();
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

  /**
   * Getter for left path list.
   * If empty generates it from data in generator.
   *
   * @return
   */
  public List<Pair<String, Boolean>> getMoreSidedLeftPaths() {
    if (moreSidedLeftPaths.isEmpty()) {
      MoreSidedDiffRequestGenerator generator = (MoreSidedDiffRequestGenerator) requestGenerator;
      moreSidedLeftPaths = generator.getLines().stream()
          .map(line ->
                   new Pair<>(line.leftPath, line.startLineRight == -1 && line.endLineRight == -1))
          .collect(Collectors.toList());

    }
    return moreSidedLeftPaths;
  }

  public RefactoringType getType() {
    return type;
  }

  public RefactoringInfo setType(RefactoringType type) {
    this.type = type;
    return this;
  }

  public String getName() {
    return type.getName();
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
    paths[0] = Utils.fixPath(leftPath);
    return this;
  }

  public String getMidPath() {
    return paths[1];
  }

  public RefactoringInfo setMidPath(String midPath) {
    paths[1] = Utils.fixPath(midPath);
    return this;
  }

  public String getRightPath() {
    return paths[2];
  }

  public RefactoringInfo setRightPath(String rightPath) {
    paths[2] = Utils.fixPath(rightPath);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RefactoringInfo)) {
      return false;
    }

    RefactoringInfo that = (RefactoringInfo) o;
    return this.getNameAfter().equals(that.getNameAfter())
        && this.getNameBefore().equals(that.getNameBefore())
        && Objects.equals(this.getDetailsAfter(), that.getDetailsAfter())
        && Objects.equals(this.getDetailsBefore(), that.getDetailsBefore())
        && Objects.equals(this.getElementBefore(), that.getElementBefore())
        && this.type == that.getType()
        && this.group == that.getGroup();
  }

  public Group getGroup() {
    return group;
  }

  public RefactoringInfo setGroup(Group group) {
    this.group = group;
    return this;
  }

  /**
   * Corrects lines if necessary.
   *
   * @param before text left window
   * @param mid    text mid window
   * @param after  text right window
   */
  public void correctLines(String before, String mid, String after) {
    boolean skipAnnotationsLeft = true;
    boolean skipAnnotationsRight = true;
    if (type.getName().matches("Add\\s(\\w)*\\sAnnotation")) {
      skipAnnotationsRight = false;
    } else if (type.getName().matches("Remove\\s(\\w)*\\sAnnotation")) {
      skipAnnotationsLeft = false;
    } else if (type.getName().matches("Modify\\s(\\w)*\\sAnnotation")) {
      skipAnnotationsLeft = skipAnnotationsRight = false;
    }
    requestGenerator.correct(before, mid, after, skipAnnotationsLeft, true, skipAnnotationsRight);
  }

  public void correctMoreSidedLines(List<String> befores, String after) {
    ((MoreSidedDiffRequestGenerator) requestGenerator).correct(befores, after, moreSidedLeftPaths,
                                                               true, false, true);
  }

  public boolean isChanged() {
    return changed;
  }

  public RefactoringInfo setChanged(boolean changed) {
    this.changed = changed;
    return this;
  }

  public FoldingDescriptor getFoldingDescriptorBefore() {
    return foldingPositions[0];
  }

  public RefactoringInfo setFoldingDescriptorBefore(FoldingDescriptor positions) {
    foldingPositions[0] = positions;
    return this;
  }

  public FoldingDescriptor getFoldingDescriptorMid() {
    return foldingPositions[1];
  }

  public RefactoringInfo setFoldingDescriptorMid(FoldingDescriptor positions) {
    foldingPositions[1] = positions;
    return this;
  }

  public FoldingDescriptor getFoldingDescriptorAfter() {
    return foldingPositions[2];
  }

  public RefactoringInfo setFoldingDescriptorAfter(FoldingDescriptor positions) {
    foldingPositions[2] = positions;
    return this;
  }
}


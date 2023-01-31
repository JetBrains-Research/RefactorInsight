package org.jetbrains.research.refactorinsight.common.data;

import com.intellij.openapi.util.Pair;
import org.jetbrains.research.refactorinsight.common.adapters.CodeRange;
import org.jetbrains.research.refactorinsight.common.diff.*;
import org.jetbrains.research.refactorinsight.common.utils.Utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private String type;
    private Group group;

    private Set<String> includes = new HashSet<>();

    private boolean hidden = false;
    private boolean threeSided = false;
    private boolean moreSided = false;

    // Optional data for move refactorings
    private boolean changed = true;

    public RefactoringInfo setIncludes(Set<String> includes) {
        this.includes = includes;
        return this;
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

    public String getType() {
        return type;
    }

    public RefactoringInfo setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return type;
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
        if (type.matches("Add\\s(\\w)*\\sAnnotation")) {
            skipAnnotationsRight = false;
        } else if (type.matches("Remove\\s(\\w)*\\sAnnotation")) {
            skipAnnotationsLeft = false;
        } else if (type.matches("Modify\\s(\\w)*\\sAnnotation")) {
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

}


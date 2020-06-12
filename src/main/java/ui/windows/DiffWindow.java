package ui.windows;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffContext;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.simple.ThreesideDiffChangeBase;
import com.intellij.diff.tools.util.base.DiffViewerListener;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import data.RefactoringEntry;
import data.RefactoringInfo;
import data.diff.ThreeSidedRange;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class DiffWindow extends com.intellij.diff.DiffExtension {

  public static Key<List<ThreeSidedRange>> REFACTORING_RANGES =
      Key.create("refactoringMiner.List<ThreeSidedRange>");
  public static Key<Boolean> REFACTORING =
      Key.create("refactoringMiner.isRefactoringDiff");


  /**
   * Requests diff window to show specific refactoring with two editors.
   *
   * @param info     RefactoringInfo
   * @param project  Current project
   */
  public static void showDiff(Collection<Change> changes, RefactoringInfo info,
                              Project project, RefactoringEntry entry) {
    List<DiffRequest> requests = entry.getRefactorings().stream()
        .filter(i -> !i.isHidden()).map(i -> i.generate(getDiffContents(changes, i, project)))
        .collect(Collectors.toList());
    DiffRequestChain chain = new SimpleDiffRequestChain(requests);
    chain.setIndex(entry.getRefactorings().stream()
        .filter(i -> !i.isHidden()).collect(Collectors.toList()).indexOf(info));
    DiffManager.getInstance().showDiff(project, chain,
        new DiffDialogHints(WindowWrapper.Mode.FRAME));
  }


  private static DiffContent[] getDiffContents(Collection<Change> changes,
                                               RefactoringInfo info, Project project) {
    if (info.getLeftPath() == null) {
      return null;
    }
    try {
      DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
      DiffContent[] contents = {null, null, null};
      for (Change change : changes) {
        if (change.getBeforeRevision() != null) {
          change.getBeforeRevision().getFile().getPath();
          if (change.getBeforeRevision().getFile().getPath().contains(info.getLeftPath())) {
            contents[0] = myDiffContentFactory.create(project,
                change.getBeforeRevision().getContent(),
                JavaClassFileType.INSTANCE);
          }
        }
        if (info.isThreeSided()
            && change.getAfterRevision() != null
            && change.getAfterRevision().getFile().getPath().contains(info.getMidPath())) {
          contents[1] = myDiffContentFactory.create(project,
              change.getAfterRevision().getContent(),
              JavaClassFileType.INSTANCE);
        }
        if (change.getAfterRevision() != null
            && change.getAfterRevision().getFile().getPath().contains(info.getRightPath())) {
          contents[2] = myDiffContentFactory.create(project,
              change.getAfterRevision().getContent(),
              JavaClassFileType.INSTANCE);
        }
      }
      return contents;

    } catch (VcsException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * IntelliJ Diff Extension.
   * This is needed to obtain the viewer object.
   * Sets a listener which is activated once classical diff is calculated and
   * code ranges can be replaced with refactoring specific ranges.
   */
  @Override
  public void onViewerCreated(@NotNull FrameDiffTool.DiffViewer viewer,
                              @NotNull DiffContext context, @NotNull DiffRequest request) {
    List<ThreeSidedRange> ranges = request.getUserData(REFACTORING_RANGES);
    if (ranges == null) {
      return;
    }
    SimpleThreesideDiffViewer myViewer = (SimpleThreesideDiffViewer) viewer;
    myViewer.addListener(new MyDiffViewerListener(myViewer, ranges));

    Boolean isRefactoring = request.getUserData(REFACTORING);
    if (isRefactoring != null) {
      myViewer.getTextSettings().setExpandByDefault(false);
    }
  }

  public static class MyDiffViewerListener extends DiffViewerListener {

    private final SimpleThreesideDiffViewer viewer;
    private final List<ThreeSidedRange> ranges;

    /**
     * EventListener for DiffWindow finishing diff calculation.
     *
     * @param viewer DiffViewer
     * @param ranges List of ThreeSidedRanges
     */
    public MyDiffViewerListener(SimpleThreesideDiffViewer viewer,
                                List<ThreeSidedRange> ranges) {
      this.ranges = ranges;
      this.viewer = viewer;
    }

    @Override
    protected void onAfterRediff() {
      List<SimpleThreesideDiffChange> oldMarkings = viewer.getChanges();
      oldMarkings.forEach(ThreesideDiffChangeBase::destroy);
      oldMarkings.clear();
      oldMarkings.addAll(ranges.stream()
          .map(r -> r.getDiffChange(viewer))
          .collect(Collectors.toList())
      );
    }
  }
}

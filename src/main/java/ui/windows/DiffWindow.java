package ui.windows;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffContext;
import com.intellij.diff.DiffManager;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.simple.ThreesideDiffChangeBase;
import com.intellij.diff.tools.util.base.DiffViewerListener;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.diff.util.ThreeSide;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import data.RefactoringInfo;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DiffWindow extends com.intellij.diff.DiffExtension {

  public static Key<RefactoringInfo> REFACTORING_INFO =
      Key.create("refactoringMiner.RefactoringInfo");

  /**
   * Requests diff window to show specific refactoring with two editors.
   *
   * @param contents array with the diffContents
   * @param info    RefactoringInfo
   * @param project Current project
   */
  public static void showDiff(DiffContent[] contents, RefactoringInfo info, Project project) {
    DiffManager.getInstance().showDiff(project, info.createDiffRequest(contents));
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
    RefactoringInfo info = request.getUserData(REFACTORING_INFO);
    if (info == null) {
      return;
    }

    SimpleThreesideDiffViewer myViewer = (SimpleThreesideDiffViewer) viewer;
    myViewer.addListener(new MyDiffViewerListener(myViewer, info));
  }

  public static class MyDiffViewerListener extends DiffViewerListener {

    private final SimpleThreesideDiffViewer viewer;
    private final RefactoringInfo info;

    /**
     * EventListener for DiffWindow finishing diff calculation.
     *
     * @param viewer DiffViewer
     * @param info   RefactoringInfo
     */
    public MyDiffViewerListener(SimpleThreesideDiffViewer viewer,
                                RefactoringInfo info) {
      this.info = info;
      this.viewer = viewer;
    }

    @Override
    protected void onAfterRediff() {
      List<SimpleThreesideDiffChange> oldMarkings = viewer.getChanges();
      oldMarkings.forEach(ThreesideDiffChangeBase::destroy);
      oldMarkings.clear();
      oldMarkings.addAll(info.getThreeSidedLineMarkings(viewer));
    }
  }
}

package ui.windows;

import com.intellij.diff.*;
import com.intellij.diff.chains.DiffRequestChain;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.simple.SimpleThreesideDiffChange;
import com.intellij.diff.tools.simple.SimpleThreesideDiffViewer;
import com.intellij.diff.tools.simple.ThreesideDiffChangeBase;
import com.intellij.diff.tools.util.base.DiffViewerListener;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import data.RefactoringEntry;
import data.RefactoringInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DiffWindow extends com.intellij.diff.DiffExtension {

  public static Key<RefactoringInfo> REFACTORING_INFO =
      Key.create("refactoringMiner.RefactoringInfo");
  public static Key<String[]> FILE_CONTENTS =
      Key.create("refactoringMiner.fileContentsArray");

  /**
   * Requests diff window to show specific refactoring with two editors.
   *
   * @param left    Left text as String
   * @param right   Right text as String
   * @param info    RefactoringInfo
   * @param project Current project
   */
  public static DiffRequest createDiff(String left, String right, RefactoringInfo info, Project project) {
    DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
    DiffContent diffContentBefore = myDiffContentFactory.create(project, left,
        JavaClassFileType.INSTANCE);
    DiffContent diffContentAfter = myDiffContentFactory.create(project, right,
        JavaClassFileType.INSTANCE);

    SimpleDiffRequest request = new SimpleDiffRequest(info.getName(),
        diffContentBefore, diffContentAfter, info.getLeftPath(), info.getRightPath());

    request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
        (text1, text2, policy, innerChanges, indicator) ->
            info.getTwoSidedLineMarkings(left, right));

    return request;
    //DiffManager.getInstance().showDiff(project, request);
  }

  /**
   * Requests diff window to show specific refactoring with three editors.
   *
   * @param left    Left text as String
   * @param mid     Mid text as String
   * @param right   Right text as String
   * @param info    RefactoringInfo
   * @param project Current project
   */
  public static DiffRequest createDiff(String left, String mid, String right, RefactoringInfo info,
                              Project project) {
    DiffContentFactoryEx myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
    DiffContent diffContentLeft = myDiffContentFactory.create(project, left,
        JavaClassFileType.INSTANCE);
    DiffContent diffContentMid = myDiffContentFactory.create(project, mid,
        JavaClassFileType.INSTANCE);
    DiffContent diffContentRight = myDiffContentFactory.create(project, right,
        JavaClassFileType.INSTANCE);

    SimpleDiffRequest request = new SimpleDiffRequest(info.getName(),
        diffContentLeft, diffContentMid, diffContentRight, info.getLeftPath(), info.getMidPath(),
        info.getRightPath());

    String[] texts = {left, mid, right};

    request.putUserData(REFACTORING_INFO, info);
    request.putUserData(FILE_CONTENTS, texts);

    return request;
    //DiffManager.getInstance().showDiff(project, request);

  }

  public static DiffRequestChain buildDiffChain(RefactoringEntry entry, Collection<Change> changes, Project project) {
    List<DiffRequest> requests = new ArrayList<>();
    for(RefactoringInfo info : entry.getRefactorings()) {
      try {
        String left = "";
        String right = "";
        for (Change change : changes) {
          if (change.getBeforeRevision() != null
                  && (project.getBasePath() + "/" + info.getLeftPath())
                  .equals(change.getBeforeRevision().getFile().getPath())) {
            left = change.getBeforeRevision().getContent();
          }
          if (change.getAfterRevision() != null
                  && (project.getBasePath() + "/" + info.getRightPath())
                  .equals(change.getAfterRevision().getFile().getPath())) {
            right = change.getAfterRevision().getContent();
          }
        }
        String mid = "";
        if (info.isThreeSided()) {
          for (Change change : changes) {
            if (change.getAfterRevision() != null
                    && (project.getBasePath() + "/" + info.getMidPath())
                    .equals(change.getAfterRevision().getFile().getPath())) {
              mid = change.getAfterRevision().getContent();
            }
          }
          requests.add(createDiff(left, mid, right, info, project));
        } else {
          requests.add(createDiff(left, right, info, project));
        }

      } catch (VcsException ex) {
        ex.printStackTrace();
      }
    }
    return new SimpleDiffRequestChain(requests);
  }

  public static void showChain(DiffRequestChain chain, Project project) {
    DiffManager.getInstance().showDiff(project, chain,
            new DiffDialogHints(WindowWrapper.Mode.FRAME));
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

    String[] texts = request.getUserData(FILE_CONTENTS);
    SimpleThreesideDiffViewer myViewer = (SimpleThreesideDiffViewer) viewer;
    myViewer.addListener(new MyDiffViewerListener(myViewer, info, texts));
  }

  public static class MyDiffViewerListener extends DiffViewerListener {

    private final SimpleThreesideDiffViewer viewer;
    private final RefactoringInfo info;
    private final String[] texts;

    /**
     * EventListener for DiffWindow finishing diff calculation.
     *
     * @param viewer DiffViewer
     * @param info   RefactoringInfo
     * @param texts  File contents in String
     */
    public MyDiffViewerListener(SimpleThreesideDiffViewer viewer,
                                RefactoringInfo info, String[] texts) {
      this.info = info;
      this.viewer = viewer;
      this.texts = texts;
    }

    @Override
    protected void onAfterRediff() {
      List<SimpleThreesideDiffChange> oldMarkings = viewer.getChanges();
      oldMarkings.forEach(ThreesideDiffChangeBase::destroy);
      oldMarkings.clear();
      List<SimpleThreesideDiffChange> newMarkings =
          info.getThreeSidedLineMarkings(texts[0], texts[1], texts[2], viewer);

      oldMarkings.addAll(newMarkings);

    }
  }
}

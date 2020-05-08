import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBViewport;
import com.intellij.vcs.log.*;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.intellij.vcs.log.util.VcsLogUtil;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.changes.GitChangeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Collections;

public class GitWindow extends ToggleAction {

  private ChangesTree changesTree;
  private JBViewport viewport;
  private boolean selected = false;
  private VcsLogGraphTable table;
  private JBLabel test;
  private MiningService miningService;
  AnActionEvent event;
  Project myProject;
  DiffContentFactoryEx myDiffContentFactory;

  private void setUp(@NotNull AnActionEvent e) {
    VcsLogChangesBrowser changesBrowser =
        (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);
    changesTree = changesBrowser.getViewer();
    MainVcsLogUi logUI = e.getData(VcsLogInternalDataKeys.MAIN_UI);

    Project currentProject = e.getProject();
    miningService = currentProject.getService(MiningService.class);
    myProject = e.getProject();
    table = logUI.getTable();
    table.getSelectionModel().addListSelectionListener(new CommitSelectionListener());
    event = e;

    myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();

    viewport = (JBViewport) changesTree.getParent();
    test = new JBLabel("TEST LABEL");
    test.setVerticalAlignment(JBLabel.CENTER);
  }

  private void toRefactoringView(@NotNull AnActionEvent e) {
    System.out.println("Button ON");
    viewport.setView(test);
  }

  private void toChangesView(@NotNull AnActionEvent e) {
    viewport.setView(changesTree);
  }

  @Override
  public boolean isSelected(@NotNull AnActionEvent e) {
    return selected;
  }

  @Override
  public void setSelected(@NotNull AnActionEvent e, boolean state) {
    if (changesTree == null) {
      setUp(e);
    }
    if (state) {
      toRefactoringView(e);
    } else {
      toChangesView(e);
    }
    selected = state;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    e.getPresentation().setVisible(true);
    e.getProject().getService(MiningService.class).loaded();
    super.update(e);
  }

  class CommitSelectionListener implements ListSelectionListener {
    @SuppressWarnings("checkstyle:CommentsIndentation")
    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
      if (listSelectionEvent.getValueIsAdjusting()) {
        return;
      }
      DefaultListSelectionModel selectionModel =
          (DefaultListSelectionModel) listSelectionEvent.getSource();

      int beginIndex = selectionModel.getMinSelectionIndex();
      int endIndex = selectionModel.getMaxSelectionIndex();

      if (beginIndex != -1 || endIndex != -1) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        for (int index = beginIndex; index <= endIndex; index++) {
          String id = table.getModel().getCommitId(index).getHash().asString();

          VirtualFile root = table.getModel().getCommitId(index).getRoot();
          Hash commitHash = table.getModel().getCommitId(index).getHash();
          Hash commitBefore = table.getModel().getCommitId(index + 1).getHash();
          var a = table.getModel().getCommitId(index + 1);

          Collection<FilePath> affectedPaths = VcsLogUtil.getAffectedPaths(root, event);
          affectedPaths = affectedPaths != null ? affectedPaths : Collections.singleton(VcsUtil.getFilePath(root));

          try {
            Collection<Change> changes = getDiff(root, affectedPaths, commitBefore, commitHash);
            for (Change change : changes) {
              String content = change.getBeforeRevision().getContent();
              DiffContent d1 = content != null ? myDiffContentFactory.create(myProject, content)
                      : myDiffContentFactory.createEmpty();
              String contentAfter = change.getAfterRevision().getContent();
              DiffContent d2 = contentAfter != null ? myDiffContentFactory.create(myProject, contentAfter)
                      : myDiffContentFactory.createEmpty();
              SimpleDiffRequest request = new SimpleDiffRequest(null, d1, d2, "Before", "After");
              DiffManager.getInstance().showDiff(myProject, request);
            }
          } catch (Exception e) {
            System.out.println(e.getStackTrace());
          }

          builder.append(id).append("<br/><ul>");
          miningService.getRefactorings(id)
              .forEach(r -> builder.append("<li>").append(r).append("</li>"));
          ;
          builder.append("</ul>");
        }
        builder.append("</html>");
        test.setText(builder.toString());

      }
    }
  }

  @NotNull
  private Collection<Change> getDiff(@NotNull VirtualFile root,
                                     @NotNull Collection<? extends FilePath> filePaths,
                                     @NotNull Hash leftRevision,
                                     @Nullable Hash rightRevision) throws VcsException {
    if (rightRevision == null) {
      return GitChangeUtils.getDiffWithWorkingDir(myProject, root, leftRevision.asString(), filePaths, false);
    }
    return GitChangeUtils.getDiff(myProject, root, leftRevision.asString(), rightRevision.asString(), filePaths);
  }

}

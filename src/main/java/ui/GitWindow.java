package ui;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.VcsCommitMetadata;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import data.RefactoringEntry;
import data.RefactoringInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.NotNull;
import services.MiningService;
import services.RefactoringsBundle;

public class GitWindow extends ToggleAction {

  Project project;
  AnActionEvent event;
  DiffContentFactoryEx myDiffContentFactory;
  private ChangesTree changesTree;
  private JBViewport viewport;
  private boolean selected = false;
  private VcsLogGraphTable table;
  private JBScrollPane scrollPane;
  private MiningService miningService;

  private void setUp(@NotNull AnActionEvent e) {
    VcsLogChangesBrowser changesBrowser =
        (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);
    changesTree = changesBrowser.getViewer();
    MainVcsLogUi logUI = e.getData(VcsLogInternalDataKeys.MAIN_UI);

    project = e.getProject();
    miningService = project.getService(MiningService.class);

    table = logUI.getTable();
    table.getSelectionModel().addListSelectionListener(new CommitSelectionListener());

    event = e;
    myDiffContentFactory = DiffContentFactoryEx.getInstanceEx();
    viewport = (JBViewport) changesTree.getParent();
    scrollPane = new JBScrollPane(new JBLabel(RefactoringsBundle.message("not.selected")));
  }

  private void toRefactoringView(@NotNull AnActionEvent e) {
    while (miningService.isMining()) {

    }
    int index = table.getSelectionModel().getAnchorSelectionIndex();
    if (index != -1) {
      buildComponent(index);
    }
    viewport.setView(scrollPane);
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
    super.update(e);
  }

  /**
   * Method called after a single commit is mined.
   * Updates the view with the refactorings found.
   *
   * @param commitId to refresh the view at.
   */
  public void refresh(String commitId) {
    int index = table.getSelectionModel().getAnchorSelectionIndex();
    if (table.getModel().getCommitId(index).getHash().asString().equals(commitId)) {
      buildComponent(index);
    }
  }

  private void buildComponent(int index) {
    String commitId = table.getModel().getCommitId(index).getHash().asString();
    VcsCommitMetadata metadata = table.getModel().getCommitMetadata(index);

    String refactorings = miningService.getRefactorings(commitId);
    RefactoringEntry entry = RefactoringEntry.fromString(refactorings);

    if (entry != null) {
      Tree tree = entry.buildTree();
      tree.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null) {
              return;
            }
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                path.getLastPathComponent();
            if (node.isLeaf()) {
              RefactoringInfo info = (RefactoringInfo)
                  node.getUserObjectPath()[1];
              showDiff(index, info);
            }
          }
        }
      });
      scrollPane.getViewport().setView(tree);
    } else {
      miningService.mineAtCommit(metadata, project, this);
    }
  }

  private void showDiff(int index, RefactoringInfo info) {
    try {
      Collection<Change> changes = table.getModel().getFullDetails(index).getChanges(0);

      String contentBefore = "";
      String contentAfter = "";
      for (Change change : changes) {
        if (change.getBeforeRevision() != null
            && (project.getBasePath() + "/" + info.getBeforePath())
            .equals(change.getBeforeRevision().getFile().getPath())) {
          contentBefore = change.getBeforeRevision().getContent();
        }
        if (change.getAfterRevision() != null
            && (project.getBasePath() + "/" + info.getAfterPath())
            .equals(change.getAfterRevision().getFile().getPath())) {
          contentAfter = change.getAfterRevision().getContent();
        }
      }

      DiffContent diffContentBefore = myDiffContentFactory.create(project, contentBefore,
          JavaClassFileType.INSTANCE);
      DiffContent diffContentAfter = myDiffContentFactory.create(project, contentAfter,
          JavaClassFileType.INSTANCE);

      SimpleDiffRequest request = new SimpleDiffRequest(info.getName(),
          diffContentBefore, diffContentAfter, info.getBeforePath(), info.getAfterPath());

      int lineCountBefore = (int) contentBefore.chars().filter(c -> c == '\n').count() + 1;
      int lineCountAfter = (int) contentAfter.chars().filter(c -> c == '\n').count() + 1;
      request.putUserData(DiffUserDataKeysEx.CUSTOM_DIFF_COMPUTER,
          (text1, text2, policy, innerChanges, indicator) ->
              info.getLineMarkings(lineCountBefore, lineCountAfter));

      DiffManager.getInstance().showDiff(project, request);
    } catch (VcsException e) {
      e.printStackTrace();
    }
  }

  class CommitSelectionListener implements ListSelectionListener {
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
        if (!miningService.isMining()) {
          buildComponent(beginIndex);
        }
      }
    }
  }
}
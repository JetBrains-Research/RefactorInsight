package ui.windows;

import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.components.JBLabel;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import services.MiningService;
import services.RefactoringsBundle;
import ui.renderer.CellRenderer;

public class GitWindow {
  private Project project;

  private ChangesTree changesTree;
  private JBViewport viewport;
  private VcsLogGraphTable table;
  private MiningService miner;
  private boolean state = false;

  /**
   * Constructor for a GitWindowInfo.
   *
   * @param e action event
   */
  public GitWindow(AnActionEvent e) {
    VcsLogChangesBrowser changesBrowser =
        (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);
    changesTree = changesBrowser.getViewer();
    viewport = (JBViewport) changesTree.getParent();
    project = e.getProject();
    miner = MiningService.getInstance(project);
    MainVcsLogUi logUI = e.getData(VcsLogInternalDataKeys.MAIN_UI);
    table = logUI.getTable();
    table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
      if (!state || listSelectionEvent.getValueIsAdjusting()) {
        return;
      }
      buildComponent();
    });
  }

  public boolean isSelected() {
    return state;
  }

  /**
   * Applies selects or deselects the tool window.
   *
   * @param state true for selected, false for unselected
   */
  public void setSelected(boolean state) {
    if (state) {
      buildComponent();
    } else {
      viewport.setView(changesTree);
    }
    this.state = state;
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
      buildComponent();
    }
  }

  private void buildComponent() {
    int index = table.getSelectionModel().getAnchorSelectionIndex();
    if (index < 0) {
      viewport.setView(new JBLabel(RefactoringsBundle.message("not.selected")));
      return;
    }
    String commitId = table.getModel().getCommitId(index).getHash().asString();
    VcsCommitMetadata metadata = table.getModel().getCommitMetadata(index);

    String refactorings = miner.getRefactorings(commitId);
    RefactoringEntry entry = RefactoringEntry.fromString(refactorings);

    if (entry == null || miner.isMining()) {
      miner.mineAtCommit(metadata, project, this);
      return;
    }

    Tree tree = entry.buildTree();
    tree.setCellRenderer(new CellRenderer());
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent ev) {
        if (ev.getClickCount() == 2) {
          TreePath path = tree.getPathForLocation(ev.getX(), ev.getY());
          if (path == null) {
            return;
          }
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)
              path.getLastPathComponent();
          if (node.isLeaf()) {
            RefactoringInfo info = (RefactoringInfo)
                node.getUserObjectPath()[1];
            SimpleDiffRequestChain chain = (SimpleDiffRequestChain) DiffWindow.buildDiffChain(entry,
                table.getModel().getFullDetails(index).getChanges(0), project);
            chain.setIndex(0);
            DiffWindow.showChain(chain, project);
          }
        }
      }
    });
    viewport.setView(tree);
  }

  private DiffRequest createDiff(int index, RefactoringInfo info) {
    try {
      Collection<Change> changes =
          table.getModel().getFullDetails(index).getChanges(0);
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
        return DiffWindow.createDiff(left, mid, right, info, project);
      } else {
        return DiffWindow.createDiff(left, right, info, project);
      }

    } catch (VcsException ex) {
      ex.printStackTrace();
    }
    return null;
  }
}

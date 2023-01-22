package org.jetbrains.research.refactorinsight.ui.windows;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.Gray;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.log.VcsCommitMetadata;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.ui.tree.Node;
import org.jetbrains.research.refactorinsight.ui.tree.TreeUtils;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.MainCellRenderer;

/**
 * Adds additional UI elements to the Git Log tab.
 * Listens to mouse events to show refactorings at selected commit.
 */
public class GitWindow {
  private Project project;
  private ChangesTree changesTree;
  private JBViewport viewport;
  private VcsLogGraphTable table;
  private MiningService miner;
  private boolean state = false;

  /**
   * Constructor for GitWindow.
   * @param p        context project.
   * @param vcsLogUi target Log tab.
   */
  public GitWindow(@NotNull Project p, @NotNull MainVcsLogUi vcsLogUi) {
    project = p;
    changesTree = Objects.requireNonNull(UIUtil.findComponentOfType(vcsLogUi.getMainComponent(),
                                                                    ChangesTree.class));
    viewport = (JBViewport) changesTree.getParent();
    table = vcsLogUi.getTable();
    miner = MiningService.getInstance(project);

    table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
      if (listSelectionEvent.getValueIsAdjusting()) {
        return;
      }
      mineIfAbsent();
      if (state) {
        buildComponent();
      }
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
    if (state && index >= 0 && table.getModel().getCommitId(index).getHash().asString().equals(commitId)) {
      buildComponent();
    }
  }

  /**
   * Mine commit if not already. Need because entry must be ready when classical diff is called.
   */
  private void mineIfAbsent() {
    int index = table.getSelectionModel().getAnchorSelectionIndex();
    if (index >= 0) {
      String commitId = table.getModel().getCommitId(index).getHash().asString();

      if (miner.get(commitId) == null) {
        VcsCommitMetadata metadata = table.getModel().getCommitMetadata(index);
        ApplicationManager.getApplication().invokeAndWait(() ->
                miner.mineAtCommit(metadata, project, this));
      }
    }
  }

  private void buildComponent() {
    int index = table.getSelectionModel().getAnchorSelectionIndex();

    if (index < 0) {
      viewport.setView(new JBList<String>());
      return;
    }

    String commitId = table.getModel().getCommitId(index).getHash().asString();
    RefactoringEntry entry = miner.get(commitId);

    if (entry == null) {
      VcsCommitMetadata metadata = table.getModel().getCommitMetadata(index);
      miner.mineAtCommit(metadata, project, this);
      return;
    }

    if (entry.timeout || entry.getRefactorings().isEmpty()) {
      final JBLabel component =
          new JBLabel(RefactorInsightBundle.message("no.ref"), SwingConstants.CENTER);
      component.setForeground(Gray._105);
      viewport.setView(component);
      return;
    }

    Tree tree = TreeUtils.buildTree(entry.getRefactorings());
    tree.setCellRenderer(new MainCellRenderer());

    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent ev) {
        if (ev.getClickCount() == 2) {
          TreePath path = tree.getPathForLocation(ev.getX(), ev.getY());
          if (path == null) {
            return;
          }
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
          if (node.isLeaf()) {
            RefactoringInfo info = ((Node) node.getUserObject()).getInfo();

            DiffWindow.showDiff(table.getModel().getFullDetails(index)
                                    .getChanges(0), info, project, entry.getRefactorings());
          }
        }
      }
    });
    viewport.setView(tree);
  }
}

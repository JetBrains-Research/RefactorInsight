package org.jetbrains.research.refactorinsight.ui.windows;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.Gray;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.log.VcsCommitMetadata;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.table.VcsLogColumn;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import icons.RefactorInsightIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.ui.tree.TreeUtils;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.MainCellRenderer;

/**
 * Is responsible for the additional ui elements in the git tool window.
 * Listens to mouse events to show refactorings at selected commit.
 */
public class GitWindow {
  private Project project;

  private ChangesTree changesTree;
  private JBViewport viewport;
  private VcsLogGraphTable table;
  private MiningService miner;
  private boolean state = false;
  private boolean labelsVisible = false;

  /**
   * Constructor for a GitWindowInfo.
   *
   * @param p        context project
   * @param vcsLogUi target log tab
   */
  public GitWindow(@NotNull Project p, @NotNull MainVcsLogUi vcsLogUi) {
    project = p;
    changesTree = Objects.requireNonNull(UIUtil.findComponentOfType(vcsLogUi.getMainComponent(),
        ChangesTree.class));
    viewport = (JBViewport) changesTree.getParent();
    table = vcsLogUi.getTable();
    miner = MiningService.getInstance(project);

    table.setDefaultRenderer(String.class, new VcsTableRefactoringRenderer(p));
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

  public boolean isLabelsVisible() {
    return labelsVisible;
  }

  public void setLabelsVisible(boolean visible) {
    labelsVisible = visible;
  }

  /**
   * Method called after a single commit is mined.
   * Updates the view with the refactorings found.
   *
   * @param commitId to refresh the view at.
   */
  public void refresh(String commitId) {
    int index = table.getSelectionModel().getAnchorSelectionIndex();
    if (index >= 0 && table.getModel().getCommitId(index).getHash().asString().equals(commitId)) {
      buildComponent();
    }
  }

  private void buildComponent() {
    int index = table.getSelectionModel().getAnchorSelectionIndex();

    if (index < 0) {
      viewport.setView(new JBList<String>());
      return;
    }

    String commitId = table.getModel().getCommitId(index).getHash().asString();

    VcsCommitMetadata metadata = table.getModel().getCommitMetadata(index);


    RefactoringEntry entry = miner.get(commitId);

    if (entry == null) {
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
            RefactoringInfo info = (RefactoringInfo)
                node.getUserObjectPath()[1];

            DiffWindow.showDiff(table.getModel().getFullDetails(index)
                .getChanges(0), info, project, entry);
          }
        }
      }
    });
    viewport.setView(tree);
  }

  /**
   * Custom cell renderer for VCSTable.
   * Enables labeling of refactored commits.
   */
  public class VcsTableRefactoringRenderer extends ColoredTableCellRenderer {

    private MiningService miner;

    public VcsTableRefactoringRenderer(Project project) {
      miner = ServiceManager.getService(project, MiningService.class);
    }

    @Override
    protected void customizeCellRenderer(JTable table, Object value, boolean selected,
                                         boolean hasFocus, int row, int column) {
      setBorder(null);
      if (value == null) {
        return;
      }

      VcsLogGraphTable graphTable = (VcsLogGraphTable) table;
      if (labelsVisible && column == graphTable.getColumnViewIndex(VcsLogColumn.DATE)) {
        String commitHash = graphTable.getModel().getCommitId(row).getHash().asString();
        if (miner.containsRefactoring(commitHash)) {
          setIcon(RefactorInsightIcons.node);
          setTransparentIconBackground(true);
        } else {
          append("      ",
              graphTable.applyHighlighters(this, row, column, hasFocus, selected));
        }
      }

      append(value.toString(),
          graphTable.applyHighlighters(this, row, column, hasFocus, selected));

      if (column == graphTable.getColumnViewIndex(VcsLogColumn.COMMIT)
          || column == graphTable.getColumnViewIndex(VcsLogColumn.AUTHOR)) {
        SpeedSearchUtil.applySpeedSearchHighlighting(table, this, false, selected);
      }
    }
  }
}

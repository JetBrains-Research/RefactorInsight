package org.jetbrains.research.refactorinsight.pullrequests;

import com.intellij.diff.util.FileEditorBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.Gray;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.HashImpl;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.util.VcsLogUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.ui.tree.TreeUtils;
import org.jetbrains.research.refactorinsight.ui.tree.renderers.MainCellRenderer;
import org.jetbrains.research.refactorinsight.ui.windows.DiffWindow;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PRFileEditor is intended to show a list of discovered refactorings in opened Pull Request.
 */
public class PRFileEditor extends FileEditorBase {
  PRVirtualFile file;
  Project project;
  JScrollPane panel;

  /**
   * Creates a new editor.
   *
   * @param project       current project.
   * @param prVirtualFile virtual file.
   */
  public PRFileEditor(Project project, PRVirtualFile prVirtualFile) {
    this.file = prVirtualFile;
    this.project = project;
    this.panel = buildComponent();
  }

  @Override
  public @NotNull JComponent getComponent() {
    return panel;
  }

  private JScrollPane buildComponent() {
    panel = new JScrollPane();
    panel.setAutoscrolls(true);
    JBViewport viewport = new JBViewport();
    viewport.add(new JScrollBar());
    viewport.setAutoscrolls(true);
    MiningService miner = MiningService.getInstance(project);

    List<RefactoringInfo> refactoringsFromAllCommits = new ArrayList<>();
    VcsLogData vcsLogData = VcsProjectLog.getInstance(project).getLogManager().getDataManager();
    VirtualFile root = vcsLogData.getRoots().iterator().next();

    for (String commitId : file.getCommitsIds()) {
      Hash hash = HashImpl.build(commitId);
      @NotNull VcsFullCommitDetails details = null;
      try {
        details = VcsLogUtil.getDetails(vcsLogData, root, hash);
      } catch (VcsException e) {
        e.printStackTrace();
      }

      miner.mineAtCommitFromPR(commitId, details.getParents().get(0).asString(), details.getTimestamp(),
          project, panel);
      RefactoringEntry entry = miner.get(commitId);
      if (entry != null) {
        refactoringsFromAllCommits.addAll(entry.getRefactorings());
      }
    }

    // Check if all commits don't have refactorings
    if (refactoringsFromAllCommits.isEmpty()) {
      final JBLabel component =
          new JBLabel(RefactorInsightBundle.message("no.ref"), SwingConstants.CENTER);
      component.setForeground(Gray._105);
      viewport.setView(component);
      panel.setViewportView(viewport);
    } else {

      Tree tree = TreeUtils.buildTree(refactoringsFromAllCommits);
      tree.setCellRenderer(new MainCellRenderer());
      tree.setAutoscrolls(true);

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
              Hash hash = HashImpl.build(info.getCommitId());
              try {
                @NotNull VcsFullCommitDetails details = VcsLogUtil.getDetails(vcsLogData, root, hash);
                @NotNull Collection<Change> changes = details.getChanges();
                DiffWindow.showDiff(changes, info, project, refactoringsFromAllCommits);
              } catch (VcsException e) {
                e.printStackTrace();
              }
            }
          }
        }
      });
      viewport.setView(tree);
      panel.setViewportView(viewport);
    }
    return panel;
  }

  @Override
  public @Nullable JComponent getPreferredFocusedComponent() {
    return panel;
  }

  @Override
  public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
    return "RefactorInsight";
  }

  @Override
  public @Nullable VirtualFile getFile() {
    return file;
  }
}

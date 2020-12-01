package org.jetbrains.research.refactorinsight.pullrequests;

import com.intellij.diff.util.FileEditorBase;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.Gray;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBLoadingPanelListener;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLogProvider;
import com.intellij.vcs.log.data.VcsLogData;
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

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PRFileEditor is intended to show a list of discovered refactorings in opened Pull Request.
 */
public class PRFileEditor extends FileEditorBase {
  private final PRVirtualFile file;
  private final Project project;
  private final JScrollPane panel;
  private JBLoadingPanel loadingPanel;
  private final ConcurrentHashMap<String, VcsFullCommitDetails> commitsDetails = new ConcurrentHashMap<>();

  /**
   * Creates a new editor.
   *
   * @param project       current project.
   * @param prVirtualFile virtual file.
   */
  public PRFileEditor(Project project, PRVirtualFile prVirtualFile) {
    this.file = prVirtualFile;
    this.project = project;
    this.panel = new JScrollPane();
    createLoadingPanel();
    collectCommitsDetails();
  }

  @Override
  public @NotNull JComponent getComponent() {
    return loadingPanel;
  }

  private void collectCommitsDetails() {
    ProgressManager.getInstance().run(new Task.Modal(
        project, "Collecting information about commits' details", true) {
      List<? extends VcsFullCommitDetails> details;

      @Override
      public void run(@NotNull ProgressIndicator progressIndicator) {
        loadingPanel.startLoading();
        VcsLogData vcsLogData = VcsProjectLog.getInstance(project).getLogManager().getDataManager();
        VirtualFile root = vcsLogData.getRoots().iterator().next();
        VcsLogProvider vcsLogProvider = VcsProjectLog.getInstance(project).getDataManager().getLogProvider(root);
        try {
          details = VcsLogUtil.getDetails(vcsLogProvider, root, file.getCommitsIds());
        } catch (VcsException e) {
          e.printStackTrace();
        }
        saveCommitsDetails(details);
      }

      @Override
      public void onFinished() {
        calculateRefactorings();
      }
    });
  }

  private void saveCommitsDetails(List<? extends VcsFullCommitDetails> vcsFullCommitDetails) {
    if (!vcsFullCommitDetails.isEmpty()) {
      for (VcsFullCommitDetails data : vcsFullCommitDetails) {
        commitsDetails.put(data.getId().asString(), data);
      }
    }
  }

  private void createLoadingPanel() {
    loadingPanel = new JBLoadingPanel(new BorderLayout(), this);
    loadingPanel.addListener(new JBLoadingPanelListener.Adapter() {
      @Override
      public void onLoadingFinish() {
        panel.updateUI();
        loadingPanel.updateUI();
      }
    });
    loadingPanel.add(panel);
  }

  private void calculateRefactorings() {
    MiningService.getInstance(project).mineAtCommitFromPR(new ArrayList<>(commitsDetails.values()),
        project, this);
  }

  public void buildComponent() {
    panel.setAutoscrolls(true);
    JBViewport viewport = new JBViewport();
    viewport.add(new JScrollBar());
    viewport.setAutoscrolls(true);
    MiningService miner = MiningService.getInstance(project);
    List<RefactoringInfo> refactoringsFromAllCommits = new ArrayList<>();

    for (String commitId : file.getCommitsIds()) {
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
              final Collection<Change> changes = Optional.ofNullable(commitsDetails.get(info.getCommitId()))
                  .map(VcsFullCommitDetails::getChanges).orElse(new ArrayList<>());
              DiffWindow.showDiff(changes, info, project, info.getEntry().getRefactorings());
            }
          }
        }
      });
      viewport.setView(tree);
    }
    panel.setViewportView(viewport);
    loadingPanel.stopLoading();
    loadingPanel.add(panel);
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

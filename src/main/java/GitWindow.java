import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBViewport;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jetbrains.annotations.NotNull;

public class GitWindow extends ToggleAction {

  private ChangesTree changesTree;
  private JBViewport viewport;
  private boolean selected = false;
  private VcsLogGraphTable table;
  private JBLabel test;
  private MiningService miningService;

  private void setUp(@NotNull AnActionEvent e) {
    VcsLogChangesBrowser changesBrowser =
        (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);
    changesTree = changesBrowser.getViewer();
    MainVcsLogUi logUI = e.getData(VcsLogInternalDataKeys.MAIN_UI);

    Project currentProject = e.getProject();
    miningService = currentProject.getService(MiningService.class);

    table = logUI.getTable();
    table.getSelectionModel().addListSelectionListener(new CommitSelectionListener());

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

}

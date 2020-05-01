import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBViewport;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;
import java.util.Map;

public class GitWindow extends ToggleAction {

    private ChangesTree changesTree;
    private JBViewport viewport;
    private boolean selected = false;
    private VcsLogGraphTable table;
    private JBLabel test;
    private Project project;
    private Map<String, List<String>> map;


    private void setUp(@NotNull AnActionEvent e) {
        VcsLogChangesBrowser changesBrowser = (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);
        MainVcsLogUi logUI = e.getData(VcsLogInternalDataKeys.MAIN_UI);

        Project currentProject = e.getProject();
        StoringService storingService = currentProject.getService(StoringService.class);
        map = storingService.getState().map;

        table = logUI.getTable();
        table.getSelectionModel().addListSelectionListener(new CommitSelectionListener());


        changesTree = changesBrowser.getViewer();
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
        if (changesTree == null) setUp(e);
        if (state) {
            toRefactoringView(e);
        } else {
            toChangesView(e);
        }
        selected = state;
    }

    class CommitSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            if (listSelectionEvent.getValueIsAdjusting()) return;
            DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) listSelectionEvent.getSource();

            int beginIndex = selectionModel.getMinSelectionIndex();
            int endIndex = selectionModel.getMaxSelectionIndex();
            if(new SampleDialogWrapper(project).showAndGet()) {
                System.out.println("Hello");
            }

            if (beginIndex != -1 || endIndex != -1) {
                StringBuilder builder  = new StringBuilder();
                builder.append("<html>");
                for(int index = beginIndex; index <= endIndex; index++) {
                    String id = table.getModel().getCommitId(index).getHash().asString();
                    builder.append(id).append("<br/><ul>");
                    if (map.get(id) == null) {
                        builder.append("<li>").append("no refactorings detected").append("</li>");
                    } else {
                        map.get(id).forEach(r -> builder.append("<li>").append(r).append("</li>"));
                    }
                    builder.append("</ul>");
                }
                builder.append("</html>");
                test.setText(builder.toString());
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(true);
        super.update(e);
    }

}

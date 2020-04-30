import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.vcs.changes.ui.ChangesTree;
import com.intellij.ui.components.JBViewport;
import com.intellij.vcs.log.ui.frame.VcsLogChangesBrowser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GitWindow extends ToggleAction {

    private ChangesTree changesTree;
    private JBViewport viewport;
    private boolean selected = false;

    private void setUp(@NotNull AnActionEvent e) {
        VcsLogChangesBrowser changesBrowser = (VcsLogChangesBrowser) e.getData(VcsLogChangesBrowser.DATA_KEY);

        changesTree = changesBrowser.getViewer();
        viewport = (JBViewport) changesTree.getParent();
    }

    private void toRefactoringView(@NotNull AnActionEvent e) {
        System.out.println("Button ON");
        JLabel test = new JLabel("TEST LABEL");
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

}

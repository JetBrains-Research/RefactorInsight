package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.services.WindowService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static java.awt.Font.*;

public class ComboBoxRefactoringAction extends ComboBoxAction implements DumbAware {

    private enum ListItem {
        FILES(RefactorInsightBundle.message("ui.ChangesBrowserBase.ComboBoxAction.list.item.files")),
        REFACTORING(RefactorInsightBundle.message("ui.ChangesBrowserBase.ComboBoxAction.list.item.refactorings"));
        public final String label;
        ListItem(String label) {
            this.label = label;
        }
    }

    private DefaultActionGroup myActions;

    private ListItem currentListItem = ListItem.FILES;

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setText(getText(getValue()));
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        ComboBoxButton button = createComboBoxButton(presentation);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setForeground(JBColor.BLUE);
        button.setMargin(JBUI.emptyInsets());
        JLabel label = new JLabel(RefactorInsightBundle.message("ui.ChangesBrowserBase.ComboBoxAction.label.text"));
        label.setFont(new Font("Default", PLAIN, button.getFont().getSize()));
        GridBagConstraints constraints = new GridBagConstraints(
                0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, JBInsets.create(0, 0), 0, 0);
        panel.add(label, constraints);
        constraints.gridx = 1;
        panel.add(button, constraints);
        return panel;
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(@NotNull JComponent button, @NotNull DataContext context) {
        if (myActions == null) {
            myActions = new DefaultActionGroup();
            for (ListItem listItem : Arrays.asList(ListItem.FILES, ListItem.REFACTORING)) {
                myActions.add(new MyAction(listItem));
            }
        }
        return myActions;
    }

    @NotNull
    private ListItem getValue() {
        return currentListItem;
    }

    private void setValue(@NotNull ListItem option) {
        if (currentListItem == option) return;
        currentListItem = option;
    }

    @Nls
    @NotNull
    private String getText(@NotNull ListItem option) {
        return option.label;
    }

    private class MyAction extends AnAction implements Toggleable, DumbAware {
        @NotNull private final ListItem myOption;

        MyAction(@NotNull ListItem option) {
            super(getText(option));
            myOption = option;
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            Toggleable.setSelected(e.getPresentation(), getValue() == myOption);
            e.getPresentation().setEnabledAndVisible(isEnabled(e));
            WindowService.getInstance(e.getProject()).update(e);
        }

        private boolean isEnabled(@NotNull AnActionEvent e) {
            return e.getProject() != null && e.getData(VcsLogInternalDataKeys.MAIN_UI) != null;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            setValue(myOption);
            Project project = e.getRequiredData(PlatformDataKeys.PROJECT);
            MainVcsLogUi vcsLogUi = e.getRequiredData(VcsLogInternalDataKeys.MAIN_UI);
            boolean state = currentListItem == ListItem.REFACTORING;
            WindowService.getInstance(project).setSelected(vcsLogUi, state);
        }
    }
}

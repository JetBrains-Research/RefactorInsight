package org.jetbrains.research.refactorinsight.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.services.WindowService;

import javax.swing.*;
import java.util.Arrays;

public class ComboBoxRefactoringAction extends ComboBoxAction implements DumbAware {

    private enum Tab {
        FILES("Files"),
        REFACTORING("Refactoring Insight");
        public final String label;
        Tab(String label) {
            this.label = label;
        }
    }

    private DefaultActionGroup myActions;

    private Tab currentTab = Tab.FILES;

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setText(getText(getValue()));
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(@NotNull JComponent button, @NotNull DataContext context) {
        if (myActions == null) {
            myActions = new DefaultActionGroup();
            for (Tab tab : Arrays.asList(Tab.FILES, Tab.REFACTORING)) {
                myActions.add(new MyAction(tab));
            }
        }
        return myActions;
    }

    @NotNull
    private Tab getValue() {
        return currentTab;
    }

    private void setValue(@NotNull Tab option) {
        if (currentTab == option) return;
        currentTab = option;
    }

    @Nls
    @NotNull
    private String getText(@NotNull Tab option) {
        return option.label;
    }

    private class MyAction extends AnAction implements Toggleable, DumbAware {
        @NotNull private final Tab myOption;

        MyAction(@NotNull Tab option) {
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
            boolean state = currentTab == Tab.REFACTORING;
            WindowService.getInstance(project).setSelected(vcsLogUi, state);
        }
    }
}

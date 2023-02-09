package org.jetbrains.research.refactorinsight.actions;

import com.intellij.diff.tools.util.FoldingModelSupport;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.services.MiningService;

public class HideNonFunctionalChangesAction extends DumbAwareToggleAction {
    private FoldingModelSupport myFoldingSupport;

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(PlatformDataKeys.PROJECT);
        return false;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        Project project = e.getRequiredData(PlatformDataKeys.PROJECT);
//        MainVcsLogUi vcsLogUi = e.getRequiredData(VcsLogInternalDataKeys.MAIN_UI);
//        VcsLogGraphTable table = vcsLogUi.getTable();
//        MiningService miner = MiningService.getInstance(project);
//
//        int index = table.getSelectionModel().getAnchorSelectionIndex();
//        String commitId = table.getModel().getCommitId(index).getHash().asString();
//        RefactoringEntry entry = miner.get(commitId);
    }
}

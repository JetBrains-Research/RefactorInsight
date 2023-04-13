package org.jetbrains.research.refactorinsight.ui;

import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.MainVcsLogUiProperties;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsLogTabsProperties;
import com.intellij.vcs.log.ui.VcsLogColorManager;
import com.intellij.vcs.log.visible.VisiblePackRefresherImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefactoringVcsLogUiFactory extends VcsLogManager.BaseVcsLogUiFactory<RefactoringVcsLogUi> {
    public RefactoringVcsLogUiFactory(@NotNull String logId, @Nullable VcsLogFilterCollection filters, @NotNull VcsLogTabsProperties uiProperties, @NotNull VcsLogColorManager colorManager) {
        super(logId, filters, uiProperties, colorManager);
    }

    @Override
    protected @NotNull RefactoringVcsLogUi createVcsLogUiImpl(@NotNull String logId, @NotNull VcsLogData logData, @NotNull MainVcsLogUiProperties properties, @NotNull VcsLogColorManager colorManager, @NotNull VisiblePackRefresherImpl refresher, @Nullable VcsLogFilterCollection filters) {
        return new RefactoringVcsLogUi(logId, logData, colorManager, properties, refresher, filters);
    }
}

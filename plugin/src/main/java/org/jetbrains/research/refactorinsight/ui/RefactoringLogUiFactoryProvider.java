package org.jetbrains.research.refactorinsight.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.VcsLogProvider;
import com.intellij.vcs.log.impl.CustomVcsLogUiFactoryProvider;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.ui.MainVcsLogUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RefactoringLogUiFactoryProvider implements CustomVcsLogUiFactoryProvider {

    @Override
    public boolean isActive(@NotNull Map<VirtualFile, VcsLogProvider> providers) {
        return true;
    }

    @Override
    public @NotNull VcsLogManager.VcsLogUiFactory<? extends MainVcsLogUi> createLogUiFactory(@NotNull String logId,
                                                                                      @NotNull VcsLogManager vcsLogManager,
                                                                                      @Nullable VcsLogFilterCollection filters) {
        return new RefactoringVcsLogUiFactory(logId, filters, vcsLogManager.getUiProperties(), vcsLogManager.getColorManager());
    }
}
package org.jetbrains.research.refactorinsight.ui;

import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.MainVcsLogUiProperties;
import com.intellij.vcs.log.ui.VcsLogColorManager;
import com.intellij.vcs.log.ui.VcsLogUiImpl;
import com.intellij.vcs.log.ui.filter.VcsLogFilterUiEx;
import com.intellij.vcs.log.ui.frame.MainFrame;
import com.intellij.vcs.log.visible.VisiblePackRefresher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefactoringVcsLogUi extends VcsLogUiImpl {
    public RefactoringVcsLogUi(@NotNull String id, @NotNull VcsLogData logData, @NotNull VcsLogColorManager manager, @NotNull MainVcsLogUiProperties uiProperties, @NotNull VisiblePackRefresher refresher, @Nullable VcsLogFilterCollection initialFilters) {
        super(id, logData, manager, uiProperties, refresher, initialFilters);
    }

    @Override
    protected @NotNull MainFrame createMainFrame(@NotNull VcsLogData logData, @NotNull MainVcsLogUiProperties uiProperties, @NotNull VcsLogFilterUiEx filterUi, boolean isEditorDiffPreview) {
//        return super.createMainFrame(logData, uiProperties, filterUi, isEditorDiffPreview);
        MyMainFrame mainFrame = new MyMainFrame(logData, this, uiProperties, filterUi, isEditorDiffPreview, this);
        return mainFrame;
    }


}

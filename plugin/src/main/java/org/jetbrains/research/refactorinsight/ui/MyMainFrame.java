package org.jetbrains.research.refactorinsight.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.util.ProgressIndicatorWithDelayedPresentation;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserBase;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.VcsLogBundle;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.impl.MainVcsLogUiProperties;
import com.intellij.vcs.log.ui.AbstractVcsLogUi;
import com.intellij.vcs.log.ui.filter.VcsLogFilterUiEx;
import com.intellij.vcs.log.ui.frame.*;
import com.intellij.vcs.log.ui.table.CommitSelectionListener;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import com.intellij.vcs.log.util.VcsLogUiUtil;
import com.intellij.vcs.log.util.VcsLogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class MyMainFrame extends MainFrame {
    private final @NotNull MyChangesBrowser myChangesBrowser;
    private final @NotNull VcsLogData myLogData;
    private boolean myIsLoading;
    private @Nullable FilePath myPathToSelect = null;

    public MyMainFrame(@NotNull VcsLogData logData,
                     @NotNull AbstractVcsLogUi logUi,
                     @NotNull MainVcsLogUiProperties uiProperties,
                     @NotNull VcsLogFilterUiEx filterUi,
                     boolean withEditorDiffPreview,
                     @NotNull Disposable disposable) {
        super(logData, logUi, uiProperties, filterUi, withEditorDiffPreview, disposable);
        myLogData = logData;

        myChangesBrowser = new MyChangesBrowser(logData.getProject(), false, false);
        myChangesBrowser.getAccessibleContext().setAccessibleName(VcsLogBundle.message("vcs.log.changes.accessible.name"));
        myChangesBrowser.getDiffAction().registerCustomShortcutSet(myChangesBrowser.getDiffAction().getShortcutSet(), getGraphTable());

        JBLoadingPanel changesLoadingPane = new JBLoadingPanel(new BorderLayout(), this,
                ProgressIndicatorWithDelayedPresentation.DEFAULT_PROGRESS_DIALOG_POSTPONE_TIME_MILLIS) {
            @Override
            public Dimension getMinimumSize() {
                return VcsLogUiUtil.expandToFitToolbar(super.getMinimumSize(), myChangesBrowser.getToolbar().getComponent());
            }
        };
        changesLoadingPane.add(myChangesBrowser);

        VcsLogGraphTable graphTable = getGraphTable();
        MyCommitSelectionListenerForDiff listenerForDiff = new MyCommitSelectionListenerForDiff(changesLoadingPane, graphTable);
        graphTable.getSelectionModel().addListSelectionListener(listenerForDiff);
        Disposer.register(this, () -> graphTable.getSelectionModel().removeListSelectionListener(listenerForDiff));

    }

//    public @NotNull ChangesBrowserBase getChangesBrowser() {
//        return myChangesBrowser;
//    }

    @Override
    public void selectFilePath(@NotNull FilePath filePath, boolean requestFocus) {
        if (myIsLoading) {
            myPathToSelect = filePath;
        }
        else {
            myChangesBrowser.getViewer().selectFile(filePath);
            myPathToSelect = null;
        }

        if (requestFocus) {
            myChangesBrowser.getViewer().requestFocus();
        }
    }

    private class MyCommitSelectionListenerForDiff extends CommitSelectionListener<VcsFullCommitDetails> {
        private final @NotNull JBLoadingPanel myChangesLoadingPane;

        protected MyCommitSelectionListenerForDiff(@NotNull JBLoadingPanel changesLoadingPane, VcsLogGraphTable graphTable) {
            super(graphTable, MyMainFrame.this.myLogData.getCommitDetailsGetter());
            myChangesLoadingPane = changesLoadingPane;
        }

        @Override
        protected void onEmptySelection() {
//            myChangesBrowser.setSelectedDetails(Collections.emptyList());
        }

        @Override
        protected void onDetailsLoaded(@NotNull List<? extends VcsFullCommitDetails> detailsList) {
            int maxSize = VcsLogUtil.getMaxSize(detailsList);
            if (maxSize > VcsLogUtil.getShownChangesLimit()) {
                String sizeText = VcsLogUtil.getSizeText(maxSize);
//                myChangesBrowser.showText(statusText -> {
//                    statusText.setText(VcsLogBundle.message("vcs.log.changes.too.many.status", detailsList.size(), sizeText));
//                    statusText.appendSecondaryText(VcsLogBundle.message("vcs.log.changes.too.many.show.anyway.status.action"),
//                            SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES,
//                            e -> myChangesBrowser.setSelectedDetails(detailsList));
//                });
            }
            else {
//                myChangesBrowser.setSelectedDetails(detailsList);
            }
        }

        @Override
        protected int @NotNull [] onSelection(int @NotNull [] selection) {
//            myChangesBrowser.resetSelectedDetails();
            return selection;
        }

        @Override
        protected void onLoadingScheduled() {
            myIsLoading = true;
            myPathToSelect = null;
        }

        @Override
        protected void onLoadingStarted() {
            myChangesLoadingPane.startLoading();
        }

        @Override
        protected void onLoadingStopped() {
            myChangesLoadingPane.stopLoading();
            myIsLoading = false;
            if (myPathToSelect != null) {
                myChangesBrowser.getViewer().selectFile(myPathToSelect);
                myPathToSelect = null;
            }
        }

        @Override
        protected void onError(@NotNull Throwable error) {
//            myChangesBrowser.showText(statusText -> statusText.setText(VcsLogBundle.message("vcs.log.error.loading.status")));
        }
    }

}


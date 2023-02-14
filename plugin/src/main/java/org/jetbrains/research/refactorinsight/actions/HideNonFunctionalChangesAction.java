package org.jetbrains.research.refactorinsight.actions;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.editor.DiffVirtualFile;
import com.intellij.diff.editor.SimpleDiffVirtualFile;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.util.DiffDataKeys;
import com.intellij.diff.tools.util.base.TextDiffSettingsHolder;
import com.intellij.diff.tools.util.text.SimpleTextDiffProvider;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HideNonFunctionalChangesAction extends DumbAwareToggleAction {
    boolean hide = false;

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return hide;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        hide = state;
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        editor.getFoldingModel().runBatchFoldingOperation(() -> {
            for (FoldRegion foldRegion : editor.getFoldingModel().getAllFoldRegions()) {
                String placeholderText = foldRegion.getPlaceholderText();
                if (placeholderText.startsWith("Moved") ||
                        placeholderText.startsWith("Pulled up") ||
                        placeholderText.startsWith("Pushed down") ||
                        placeholderText.startsWith("Inlined to ") ||
                        placeholderText.startsWith("Extracted from ")) {
                    foldRegion.setExpanded(!hide);
                }
            }
        });
    }

    private void getDiff(@NotNull AnActionEvent e) {
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        VirtualFile virtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        DiffRequest request = e.getRequiredData(DiffDataKeys.DIFF_REQUEST);
        DiffVirtualFile file = new SimpleDiffVirtualFile(request);

        //TODO: get CharSequence or Psi of files

//        List<DiffContent> contents = ((SimpleDiffRequest) request).getContents();
//        DiffContent leftContent = contents.get(0);
//        DiffContent rightContent = contents.get(1);


//        TextDiffSettingsHolder.TextDiffSettings settings = TextDiffSettingsHolder.TextDiffSettings.getSettings();
//        Disposable disposable = Disposer.newDisposable();

//        SmartTextDiffProvider.create();
//        SimpleTextDiffProvider.compareRange()
    }
}

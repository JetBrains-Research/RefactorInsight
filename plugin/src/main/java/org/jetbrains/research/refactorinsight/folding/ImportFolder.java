package org.jetbrains.research.refactorinsight.folding;

import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.tools.simple.SimpleDiffChange;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.diff.util.Side;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImportFolder {
    static Set<FoldRegion> foldRegions;

    static {
        foldRegions = new HashSet<>();
    }

    public static boolean isImportFoldRegion(FoldRegion region) {
        return foldRegions.contains(region);
    }

    public static void foldImports(@NotNull FrameDiffTool.DiffViewer viewer) {
        if (!(viewer instanceof SimpleDiffViewer diffViewer)) return;

        Project project = diffViewer.getProject();
        if (project == null) return;

        List<SimpleDiffChange> changes = diffViewer.getDiffChanges();
        addFold(project, diffViewer.getEditor1(), changes, Side.LEFT);
        addFold(project, diffViewer.getEditor2(), changes, Side.RIGHT);
    }

    private static void addFold(@NotNull Project project, @NotNull Editor editor,
                                @NotNull List<SimpleDiffChange> changes, @NotNull Side side) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) return;

        List<TextRange> result = new ArrayList<>();
        psiFile.accept(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element.getTextLength() == 0) return;

                if (element instanceof PsiImportStatement) {
                    TextRange range = getRangeOfChange(element.getTextRange(), changes, side);
                    if (range != null) result.add(range);
                }
                else {
                    element.acceptChildren(this);
                }
            }
        });

        editor.getFoldingModel().runBatchFoldingOperation(() -> {
            for (TextRange range : result) {
                FoldRegion value = editor.getFoldingModel()
                        .addFoldRegion(range.getStartOffset(), range.getEndOffset(), "");
                if (value != null) {
                    value.setExpanded(false);
                    value.setInnerHighlightersMuted(true);
                }
                foldRegions.add(value);
            }
        });
    }

    private static TextRange getRangeOfChange(@NotNull TextRange elementTextRange, @NotNull List<SimpleDiffChange> changes,
                                              @NotNull Side side) {
        for (SimpleDiffChange change : changes) {
            LineFragment fragment = change.getFragment();
            int startOffset = side == Side.LEFT ? fragment.getStartOffset1() : fragment.getStartOffset2();
            int endOffset = side == Side.LEFT ? fragment.getEndOffset1() : fragment.getEndOffset2();
            if (elementTextRange.getStartOffset() >= startOffset &&
                    elementTextRange.getEndOffset() <= endOffset)
                return new TextRange(startOffset, endOffset);
        }
        return null;
    }
}

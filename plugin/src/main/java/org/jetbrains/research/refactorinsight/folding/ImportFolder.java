package org.jetbrains.research.refactorinsight.folding;

import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.diff.tools.simple.SimpleDiffChange;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.diff.util.Side;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.research.refactorinsight.actions.HideNonFunctionalChangesAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Folds discovered imports in code diffs when Hide Non-functional Changes Checkbox Action is enabled.
 */
public class ImportFolder {
    static Set<FoldRegion> foldRegions = new HashSet<>();

    public static boolean isImportFoldRegion(FoldRegion region) {
        return foldRegions.contains(region);
    }

    public static void foldImports(@NotNull FrameDiffTool.DiffViewer viewer) {
        if (!(viewer instanceof SimpleDiffViewer diffViewer)) return;

        Project project = diffViewer.getProject();
        if (project == null) return;

        DocumentContent content1 = diffViewer.getContent1();
        DocumentContent content2 = diffViewer.getContent2();
        final Document document1 = content1.getDocument();
        final Document document2 = content2.getDocument();

        CharSequence[] texts = ReadAction.compute(() -> new CharSequence[]{
                document1.getImmutableCharSequence(),
                document2.getImmutableCharSequence()
        });

        ProgressIndicator indicator = new EmptyProgressIndicator();
        List<LineFragment> lineFragments = diffViewer.getTextDiffProvider().compare(texts[0], texts[1], indicator);
        if (lineFragments == null) return;

        List<SimpleDiffChange> changes = new ArrayList<>();
        for (LineFragment fragment : lineFragments) {
            changes.add(new SimpleDiffChange(changes.size(), fragment));
        }

        modifyEditor(project, diffViewer.getEditor1(), changes, Side.LEFT);
        modifyEditor(project, diffViewer.getEditor2(), changes, Side.RIGHT);
    }

    private static void modifyEditor(@NotNull Project project,
                                     @NotNull Editor editor,
                                     @NotNull List<SimpleDiffChange> changes,
                                     @NotNull Side side) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) return;

        List<TextRange> result = new ArrayList<>();
        psiFile.accept(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element.getTextLength() == 0) return;

                if (isImport(element)) {
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
                    boolean hide = HideNonFunctionalChangesAction.isHide();
                    value.setExpanded(!hide);
                    value.setInnerHighlightersMuted(true);
                }
                foldRegions.add(value);
            }
        });
    }

    private static boolean isImport(PsiElement element) {
        Language elementLanguage = element.getLanguage();
        if (elementLanguage.equals(JavaLanguage.INSTANCE)) {
            return element instanceof PsiImportStatement;
        } else if ("kotlin".equalsIgnoreCase(elementLanguage.getID())) {
            return element instanceof KtImportDirective;
        }
        return false;
    }

    private static TextRange getRangeOfChange(@NotNull TextRange elementTextRange,
                                              @NotNull List<SimpleDiffChange> changes,
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

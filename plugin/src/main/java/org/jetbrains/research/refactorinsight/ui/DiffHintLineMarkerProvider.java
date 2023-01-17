package org.jetbrains.research.refactorinsight.ui;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.GutterName;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo;
import com.intellij.diff.DiffContentFactoryImpl;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import icons.RefactorInsightIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.ui.windows.DiffWindow;

import javax.swing.Icon;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
public class DiffHintLineMarkerProvider extends LineMarkerProviderDescriptor {
    private static final String DIFF_WINDOW_CLASS_NAME_PREFIX = DiffContentFactoryImpl.class.getName() + "$";
    @Override
    public @Nullable("null means disabled")
    @GutterName String getName() {
        return "RefactorInsight gutter";
    }
    @Override
    public @Nullable Icon getIcon() {
        return RefactorInsightIcons.toggle;
    }
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }
    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements,
                                       @NotNull Collection<? super LineMarkerInfo<?>> result) {
        String commitId = extractCommitId(elements.get(0));
        for (PsiElement element : elements) {
            //TODO: add plugin's gutter close to lines containing refactoring changes in code diffs
            if (isIdentifier(element)) {
                RefactoringInfoHint info = new RefactoringInfoHint(element, e -> "Refactoring detected");
                result.add(info);
            }
        }
    }

    private String extractCommitId(PsiElement element) {
        VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
        if (virtualFile.getClass().getName().startsWith(DIFF_WINDOW_CLASS_NAME_PREFIX)) {
            return virtualFile.getUserData(Keys.COMMIT_ID);
        }
        return null;
    }

    private boolean isIdentifier(PsiElement element) {
        Language elementLanguage = element.getLanguage();
        PsiElement elementParent = element.getParent();
        if (elementLanguage.equals(JavaLanguage.INSTANCE)) {
            return element instanceof PsiIdentifier && elementParent instanceof PsiMethod;
        } else if ("kotlin".equalsIgnoreCase(elementLanguage.getID())) {
            return element instanceof LeafElement leaf && "IDENTIFIER".equals(leaf.getElementType().toString());
        }
        return false;
    }
    private static class RefactoringInfoHint extends MergeableLineMarkerInfo<PsiElement> {
        RefactoringInfoHint(@NotNull final PsiElement element, Function<? super PsiElement, String> tooltipProvider) {
            super(element,
                    element.getTextRange(),
                    RefactorInsightIcons.toggle,
                    tooltipProvider,
                    MyIconGutterHandler.INSTANCE,
                    GutterIconRenderer.Alignment.LEFT,
                    () -> tooltipProvider.fun(element));
        }
        @Override
        public boolean canMergeWith(@NotNull MergeableLineMarkerInfo<?> info) {
            return info instanceof RefactoringInfoHint;
        }
        @Override
        public Icon getCommonIcon(@NotNull List<? extends MergeableLineMarkerInfo<?>> infos) {
            return RefactorInsightIcons.toggle;
        }
        @NotNull
        @Override
        public Function<? super PsiElement, String> getCommonTooltip(@NotNull List<? extends MergeableLineMarkerInfo<?>> infos) {
            return __ -> "Refactoring detected";
        }
    }
    private static class MyIconGutterHandler implements GutterIconNavigationHandler<PsiElement> {
        static final MyIconGutterHandler INSTANCE = new MyIconGutterHandler();
        @Override
        public void navigate(MouseEvent e, PsiElement nameIdentifier) {
            final PsiElement listOwner = nameIdentifier.getParent();
            final PsiFile containingFile = listOwner.getContainingFile();
            final VirtualFile virtualFile = PsiUtilCore.getVirtualFile(listOwner);
            if (virtualFile != null && containingFile != null) {
                final JBPopup popup = createTextPopup();
                popup.show(new RelativePoint(e));
            }
        }
        @NotNull
        private static JBPopup createTextPopup() {
            //TODO: show refactoring description
            return JBPopupFactory.getInstance().createMessage("Changes");
        }
    }
}

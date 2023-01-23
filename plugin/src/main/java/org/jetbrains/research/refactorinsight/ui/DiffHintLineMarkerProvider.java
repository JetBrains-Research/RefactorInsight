package org.jetbrains.research.refactorinsight.ui;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.GutterName;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo;
import com.intellij.diff.DiffContentFactoryImpl;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import com.intellij.util.ui.JBEmptyBorder;
import git4idea.GitCommit;
import git4idea.history.GitHistoryUtils;
import icons.RefactorInsightIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.RefactorInsightBundle;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;
import org.jetbrains.research.refactorinsight.ui.windows.DiffWindow;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.*;

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
        if (elements.isEmpty()) return;
        List<RefactoringInfo> refactoringInfos = getRefactoringInfos(elements.get(0));
        if (refactoringInfos == null) return;

        boolean isRight = isRightPartOfDiff(elements.get(0));
        Map<Integer, Set<RefactoringInfo>> refactoringsMap = new HashMap<>();

        for (PsiElement element : elements) {
            if (!isIdentifier(element)) continue;

            int lineNumber = getLineNumber(element);
            int textOffset = element.getTextOffset();
            for (RefactoringInfo refactoringInfo : refactoringInfos) {
                refactoringsMap.putIfAbsent(lineNumber, new HashSet<>());
                if (refactoringInfo.containsElement(lineNumber, textOffset, isRight) &&
                        !refactoringsMap.get(lineNumber).contains(refactoringInfo)) {
                    RefactoringInfoHint info = new RefactoringInfoHint(element,
                            e -> RefactorInsightBundle.message("refactoring.detected.hint"), refactoringInfo);
                    result.add(info);
                    refactoringsMap.get(lineNumber).add(refactoringInfo);
                }
            }
        }
    }

    private int getLineNumber(PsiElement element) {
        FileViewProvider fileViewProvider = element.getContainingFile().getViewProvider();
        Document document = fileViewProvider.getDocument();
        int textOffset = element.getTextOffset();
        return document.getLineNumber(textOffset);
    }

    private boolean isRightPartOfDiff(PsiElement element) {
        VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
        return virtualFile.getUserData(Keys.CHILD_COMMIT_ID) == null;
    }

    private List<RefactoringInfo> getRefactoringInfos(PsiElement element) {
        VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
        String commitId;
        if (virtualFile.getClass().getName().startsWith(DIFF_WINDOW_CLASS_NAME_PREFIX)) {
            if (virtualFile.getUserData(Keys.CHILD_COMMIT_ID) == null)
                commitId = virtualFile.getUserData(Keys.COMMIT_ID);
            else
                commitId = virtualFile.getUserData(Keys.CHILD_COMMIT_ID);
            if (commitId == null) return null;
            MiningService miner = MiningService.getInstance(element.getProject());
            RefactoringEntry entry = miner.get(commitId);
            if (entry == null) return null;
            return entry.getRefactorings().stream().filter(ref -> fromSameFile(element, ref)).toList();
        }
        return null;
    }

    private boolean fromSameFile(PsiElement element, RefactoringInfo refactoringInfo) {
        String elementPath = element.getContainingFile().getVirtualFile().getPath();
        String refactoringClassPath = refactoringInfo.getRightPath();
        return elementPath.endsWith(refactoringClassPath);
    }

    private boolean isIdentifier(PsiElement element) {
        Language elementLanguage = element.getLanguage();
        if (elementLanguage.equals(JavaLanguage.INSTANCE)) {
            return element instanceof PsiIdentifier;
        } else if ("kotlin".equalsIgnoreCase(elementLanguage.getID())) {
            return element instanceof LeafElement leaf && "IDENTIFIER".equals(leaf.getElementType().toString());
        }
        return false;
    }

    private static class RefactoringInfoHint extends MergeableLineMarkerInfo<PsiElement> {

        RefactoringInfoHint(@NotNull final PsiElement element, Function<? super PsiElement, String> tooltipProvider,
                            RefactoringInfo refactoringInfo) {
            super(element,
                    element.getTextRange(),
                    RefactorInsightIcons.toggle,
                    tooltipProvider,
                    new MyIconGutterHandler(refactoringInfo),
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
            return __ -> RefactorInsightBundle.message("refactoring.detected.hint");
        }
    }

    private static class MyIconGutterHandler implements GutterIconNavigationHandler<PsiElement> {

        private final RefactoringInfo refactoringInfo;

        MyIconGutterHandler(RefactoringInfo refactoringInfo) {
            this.refactoringInfo = refactoringInfo;
        }

        @Override
        public void navigate(MouseEvent e, PsiElement nameIdentifier) {
            final PsiElement listOwner = nameIdentifier.getParent();
            final PsiFile containingFile = listOwner.getContainingFile();
            final VirtualFile virtualFile = PsiUtilCore.getVirtualFile(listOwner);

            if (virtualFile != null && containingFile != null) {
                final JBPopup popup = createTextPopup(nameIdentifier.getProject());
                popup.show(new RelativePoint(e));
            }
        }

        @NotNull
        private JBPopup createTextPopup(Project project) {
            return JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(createComponent(project), null)
                    .createPopup();
        }

        @NotNull
        private JComponent createComponent(Project project) {
            JButton showDiffButton = new JButton();
            showDiffButton.setIcon(AllIcons.Actions.Diff);

            showDiffButton.addActionListener(e -> ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    String basePath = project.getBasePath();
                    if (basePath == null) return;
                    VirtualFile root = LocalFileSystem.getInstance().findFileByPath(basePath);
                    if (root == null) return;
                    List<GitCommit> history = GitHistoryUtils.history(project, root, refactoringInfo.getCommitId(), "-1");
                    if (history.isEmpty()) return;

                    DiffWindow.showDiff(history.get(0).getChanges(), refactoringInfo, project, refactoringInfo.getEntry().getRefactorings());
                } catch (VcsException ex) {
                    throw new RuntimeException(ex);
                }
            }));

            showDiffButton.setSize(26, 24);
            showDiffButton.setBorder(new JBEmptyBorder(1, 2, 1, 2));
            showDiffButton.setToolTipText(RefactorInsightBundle.message("show.diff.tooltip"));
            JLabel refactoringDescription = new JLabel(refactoringInfo.getType());
            JPanel panel = new JPanel();
            panel.add(refactoringDescription);
            panel.add(showDiffButton);
            return panel;
        }
    }
}

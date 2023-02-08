package org.jetbrains.research.refactorinsight.ui;

import com.intellij.codeInsight.daemon.*;
import com.intellij.diff.DiffContentFactoryImpl;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.SmartList;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
        if (refactoringInfos == null || refactoringInfos.isEmpty()) return;

        boolean isRight = isRightPartOfDiff(elements.get(0));
        Map<Integer, Set<RefactoringWithPsiElement>> lineToRefactorings = new HashMap<>();

        for (PsiElement element : elements) {
            if (!isIdentifier(element)) continue;
            int lineNumber = getLineNumber(element);
            int textOffset = element.getTextOffset();
            for (RefactoringInfo refactoringInfo : refactoringInfos) {
                if (refactoringInfo.containsElement(lineNumber, textOffset, isRight)) {
                    RefactoringWithPsiElement refactoringWithPsiElement =
                            new RefactoringWithPsiElement(refactoringInfo, element);
                    lineToRefactorings.computeIfAbsent(lineNumber, __ -> new LinkedHashSet<>())
                            .add(refactoringWithPsiElement);
                }
            }
        }
        for (Set<RefactoringWithPsiElement> value : lineToRefactorings.values()) {
            var psiElements = value.stream().map(it -> it.element).collect(Collectors.toSet());
            var refactorings = value.stream().map(it -> it.info).toList();
            result.add(new RefactoringInfoHint(new SmartList<>(psiElements), refactorings));
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
            boolean isRight = virtualFile.getUserData(Keys.CHILD_COMMIT_ID) == null;
            if (isRight)
                commitId = virtualFile.getUserData(Keys.COMMIT_ID);
            else
                commitId = virtualFile.getUserData(Keys.CHILD_COMMIT_ID);
            if (commitId == null) return null;
            MiningService miner = MiningService.getInstance(element.getProject());
            RefactoringEntry entry = miner.get(commitId);
            if (entry == null) return null;
            return entry.getRefactorings().stream().filter(ref -> fromSameFile(element, ref, isRight)).toList();
        }
        return null;
    }

    private boolean fromSameFile(PsiElement element, RefactoringInfo refactoringInfo, boolean isRight) {
        String elementPath = element.getContainingFile().getVirtualFile().getPath();
        String refactoringClassPath = isRight ? refactoringInfo.getRightPath() : refactoringInfo.getLeftPath();
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

    private record RefactoringWithPsiElement(RefactoringInfo info, PsiElement element) {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof RefactoringWithPsiElement other && info.equals(other.info);
        }

        @Override
        public int hashCode() {
            return info.hashCode();
        }
    }

    private static class RefactoringInfoHint extends LineMarkerInfo<PsiElement> {
        public RefactoringInfoHint(List<PsiElement> psiElements, List<RefactoringInfo> refactoringInfos) {
            super(psiElements.get(0),
                    getCommonTextRange(psiElements),
                    RefactorInsightIcons.toggle,
                    __ -> RefactorInsightBundle.message("refactoring.detected.hint"),
                    getCommonNavigationHandler(refactoringInfos),
                    GutterIconRenderer.Alignment.LEFT,
                    () -> RefactorInsightBundle.message("refactoring.detected.hint"));
        }

        private static TextRange getCommonTextRange(List<PsiElement> psiElements) {
            int startOffset = Integer.MAX_VALUE;
            int endOffset = Integer.MIN_VALUE;
            for (PsiElement element : psiElements) {
                startOffset = Math.min(startOffset, element.getTextRange().getStartOffset());
                endOffset = Math.max(endOffset, element.getTextRange().getEndOffset());
            }
            return TextRange.create(startOffset, endOffset);
        }

        private static GutterIconNavigationHandler<PsiElement> getCommonNavigationHandler(
                List<RefactoringInfo> refactoringInfos) {
            return (mouseEvent, psiElement) -> {
                BaseListPopupStep<RefactoringInfo> step = new BaseListPopupStep<>(
                        RefactorInsightBundle.message("refactoring.list.title"), refactoringInfos) {
                    @Override
                    public Icon getIconFor(final RefactoringInfo info) {
                        switch (info.getGroup()) {
                            case METHOD -> {
                                return AllIcons.Nodes.Method;
                            }
                            case CLASS -> {
                                return AllIcons.Nodes.Class;
                            }
                            case ATTRIBUTE -> {
                                return AllIcons.Nodes.ObjectTypeAttribute;
                            }
                            case VARIABLE -> {
                                return AllIcons.Nodes.Variable;
                            }
                            case INTERFACE -> {
                                return AllIcons.Nodes.Interface;
                            }
                            case ABSTRACT -> {
                                return AllIcons.Nodes.AbstractClass;
                            }
                            case PACKAGE -> {
                                return AllIcons.Nodes.Package;
                            }
                            default -> {
                                return RefactorInsightIcons.toggle;
                            }
                        }
                    }

                    @Override
                    @NotNull
                    public String getTextFor(final RefactoringInfo info) {
                        return StringUtil.first(info.getType(), 100, true).replace('\n', ' ');
                    }

                    @Override
                    public PopupStep<?> onChosen(final RefactoringInfo info, final boolean finalChoice) {
                        return doFinalStep(() -> showDiff(psiElement, info));
                    }
                };
                JBPopupFactory.getInstance().createListPopup(step).show(new RelativePoint(mouseEvent));
            };
        }

        private static void showDiff(final PsiElement psiElement, RefactoringInfo refactoringInfo) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    Project project = psiElement.getProject();
                    String basePath = project.getBasePath();
                    if (basePath == null) return;
                    VirtualFile root = LocalFileSystem.getInstance().findFileByPath(basePath);
                    if (root == null) return;
                    List<GitCommit> history = GitHistoryUtils.history(
                            project,
                            root,
                            refactoringInfo.getCommitId(),
                            "-1"
                    );
                    if (history.isEmpty()) return;

                    DiffWindow.showDiff(
                            history.get(0).getChanges(),
                            refactoringInfo,
                            project,
                            refactoringInfo.getEntry().getRefactorings()
                    );
                } catch (VcsException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
}

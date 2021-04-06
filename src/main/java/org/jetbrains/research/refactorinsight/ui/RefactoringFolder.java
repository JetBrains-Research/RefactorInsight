package org.jetbrains.research.refactorinsight.ui;

import com.intellij.application.options.colors.highlighting.RendererWrapper;
import com.intellij.codeInsight.daemon.impl.HintRenderer;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringEntry;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;

import java.util.List;

abstract public class RefactoringFolder {



  public static void foldRefactorings(SimpleDiffViewer viewer, String commitId) {
    RefactoringEntry refactoringEntry =
        MiningService.getInstance(viewer.getContext().getProject()).get(commitId);
    List<RefactoringInfo> refactorings = refactoringEntry.getRefactorings();

    //TODO: Add a filtration of refactorings by types, process only Move, Pull Up and Push Down Method.
    //TODO: Check why getType() always returns null
    refactorings.stream()
        .filter(r -> r.getName().equals(RefactoringType.MOVE_OPERATION.getName()))
        .forEach(r -> {
              String methodNameBefore = r.getNameBefore();
              PsiElement[] children =
                  PsiDocumentManager
                      .getInstance(viewer.getProject())
                      .getPsiFile(viewer.getEditor1().getDocument())
                      .getChildren();
              for (PsiElement element : children) {
                if (element instanceof PsiClass) {
                  PsiClass psiClass = (PsiClass) element;
                  PsiMethod[] methods = psiClass.getMethods();
                  for (PsiMethod psiMethod : methods) {
                    if (methodNameBefore.contains(psiMethod.getName())) {
                      //Adds a folding block to the left side of the diff window for the moved method.
                      viewer.getEditor1().getFoldingModel().runBatchFoldingOperation(
                          () -> {
                            //TODO: Customize the text based on refactoring type
                            // (method was Moved/Pulled Up/Pushed Down)

                            //TODO: Check if method was changed or not
                            // and add information about it (with changes/no changes)
                            FoldRegion value = viewer.getEditor1().getFoldingModel()
                                .addFoldRegion(psiMethod.getBody().getTextRange().getStartOffset(),
                                    psiMethod.getTextRange().getEndOffset(),
                                    "");
                            if (value != null) {
                              value.setExpanded(false);
                              value.setInnerHighlightersMuted(true);
                            }

                            String className = r.getDetailsAfter();
                            String hintText =
                                String.format("Moved to %s",
                                    className.substring(
                                        className.lastIndexOf(".") + 1).trim() + " class.");
                            RendererWrapper renderer = new RendererWrapper(new HintRenderer(hintText), false);

                            viewer.getEditor1().getInlayModel().addBlockElement(
                                psiMethod.getTextRange().getStartOffset() - 3,
                                true, true, 1,
                                renderer);
                          });
                    }
                  }
                }
              }
            }
        );
  }
}

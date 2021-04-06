package org.jetbrains.research.refactorinsight.ui;

import com.intellij.application.options.colors.highlighting.RendererWrapper;
import com.intellij.codeInsight.daemon.impl.HintRenderer;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.util.side.OnesideTextDiffViewer;
import com.intellij.diff.tools.util.side.ThreesideTextDiffViewer;
import com.intellij.diff.tools.util.side.TwosideTextDiffViewer;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.services.MiningService;

import java.util.List;
import java.util.stream.Collectors;


abstract public class RefactoringFolder {
  public static void foldRefactorings(@NotNull FrameDiffTool.DiffViewer viewer, @NotNull DiffRequest request) {
    if (request instanceof SimpleDiffRequest) {
      SimpleDiffRequest diffRequest = (SimpleDiffRequest) request;
      String commitId = diffRequest.getContentTitles().get(1);
      if (viewer instanceof OnesideTextDiffViewer) {
        RefactoringFolder.foldRefactorings((OnesideTextDiffViewer) viewer, commitId);
      } else if (viewer instanceof TwosideTextDiffViewer) {
        RefactoringFolder.foldRefactorings((TwosideTextDiffViewer) viewer, commitId);
      } else if (viewer instanceof ThreesideTextDiffViewer) {
        RefactoringFolder.foldRefactorings((ThreesideTextDiffViewer) viewer, commitId);
      }
    }
  }

  private static void foldRefactorings(@NotNull OnesideTextDiffViewer viewer, String commitId) {
    List<RefactoringInfo> foldableRefactorings =
        MiningService.getInstance(viewer.getProject()).get(commitId).getRefactorings()
            .stream().filter(RefactoringFolder::foldable).collect(Collectors.toList());

    Editor editor = viewer.getEditor();

    modifyEditor(editor, foldableRefactorings, true);
    modifyEditor(editor, foldableRefactorings, false);
  }

  private static void foldRefactorings(@NotNull TwosideTextDiffViewer viewer, String commitId) {
    List<RefactoringInfo> foldableRefactorings =
        MiningService.getInstance(viewer.getProject()).get(commitId).getRefactorings()
            .stream().filter(RefactoringFolder::foldable).collect(Collectors.toList());

    modifyEditor(viewer.getEditor1(), foldableRefactorings, true);
    modifyEditor(viewer.getEditor2(), foldableRefactorings, false);
  }

  private static void foldRefactorings(@NotNull ThreesideTextDiffViewer viewer, String commitId) {
    List<RefactoringInfo> foldableRefactorings =
        MiningService.getInstance(viewer.getProject()).get(commitId).getRefactorings()
            .stream().filter(RefactoringFolder::foldable).collect(Collectors.toList());

    List<? extends EditorEx> editor = viewer.getEditors();

    modifyEditor(editor.get(0), foldableRefactorings, true);
    modifyEditor(editor.get(1), foldableRefactorings, false);
    modifyEditor(editor.get(2), foldableRefactorings, true);
  }

  private static void modifyEditor(@NotNull Editor editor, @NotNull List<RefactoringInfo> foldableRefactorings, boolean before) {
    PsiFile psiFile = PsiDocumentManager
        .getInstance(editor.getProject())
        .getPsiFile(editor.getDocument());

    foldableRefactorings.forEach(info -> {
          Element elementType = getElementType(info);
          Positions positions = findPositions(psiFile, elementType,
              before ? info.getNameBefore() : info.getNameAfter(),
              before ? info.getDetailsBefore() : info.getDetailsAfter());
          String hintText = makeHintText(info, elementType, before);
          if (positions != null) {
            editor.getFoldingModel().runBatchFoldingOperation(
                () -> {
                  //TODO: Check if method was changed or not
                  // and add information about it (with changes/no changes)

                  FoldRegion value = editor.getFoldingModel()
                      .addFoldRegion(positions.foldingStartOffset, positions.foldingEndOffset, "");
                  if (value != null) {
                    value.setExpanded(false);
                    value.setInnerHighlightersMuted(true);
                  }

                  RendererWrapper renderer = new RendererWrapper(new HintRenderer(hintText), false);
                  editor.getInlayModel().addBlockElement(
                      positions.hintOffset,
                      true, true, 1,
                      renderer);
                });
          }
        }
    );
  }

  @NotNull
  private static String makeHintText(@NotNull RefactoringInfo info, @NotNull Element elementType, boolean before) {
    String details = before ? info.getDetailsAfter() : info.getDetailsBefore();
    String prefix = operationName(info) + (before ? " to " : " from ");
    switch (elementType) {
      case CLASS:
        return prefix + details;
      case METHOD:
      case FIELD:
        return prefix + details.substring(details.lastIndexOf(".") + 1);
    }
    throw new AssertionError();
  }

  @NotNull
  private static String operationName(@NotNull RefactoringInfo info) {
    if (info.getName().contains("Move")) {
      return "Moved";
    } else if (info.getName().contains("Pull Up")) {
      return "Pulled Up";
    } else if (info.getName().contains("Push Down")) {
      return "Pushed Down";
    }
    throw new AssertionError("Unexpected operation");
  }

  @NotNull
  private static Element getElementType(@NotNull RefactoringInfo info) {
    if (info.getName().endsWith("Class")) {
      return Element.CLASS;
    } else if (info.getName().endsWith("Method")) {
      return Element.METHOD;
    } else if (info.getName().endsWith("Attribute")) {
      return Element.FIELD;
    }
    throw new AssertionError("Unexpected element");
  }

  @Nullable
  private static Positions findPositions(@NotNull PsiFile psiFile, @NotNull Element element, String name, String details) {
    switch (element) {
      case CLASS: {
        PsiClass psiClass = findClass(psiFile, name);
        if (psiClass != null) {
          return new Positions(
              psiClass.getTextRange().getStartOffset(),
              psiClass.getLBrace().getTextOffset(),
              psiClass.getRBrace().getTextOffset() + 1
          );
        }
        break;
      }
      case FIELD: {
        PsiField psiField = findField(psiFile, name, details);
        if (psiField != null) {
          return new Positions(
              psiField.getTextRange().getStartOffset(),
              psiField.getTextRange().getEndOffset(),
              psiField.getTextRange().getEndOffset()

          );
        }
        break;
      }
      case METHOD: {
        PsiMethod psiMethod = findMethod(psiFile, name);
        if (psiMethod != null) {
          return new Positions(
              psiMethod.getTextRange().getStartOffset() - 3,
              psiMethod.getBody().getTextRange().getStartOffset(),
              psiMethod.getTextRange().getEndOffset());
        }
        break;
      }
    }
    return null;
  }

  @Nullable
  private static PsiMethod findMethod(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    PsiClass psiClass = findClass(psiFile, qualifiedName.substring(0, qualifiedName.lastIndexOf(".")));
    if (psiClass != null) {
      PsiMethod[] psiMethods = psiClass.findMethodsByName(
          qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1, qualifiedName.indexOf("(")), false);
      if (psiMethods.length > 0) {
        if (psiMethods.length > 1) {
          // TODO:
          System.err.println("Multiple methods is not implemented");
        }
        return psiMethods[0];
      }
    }
    return null;
  }

  @Nullable
  private static PsiField findField(@NotNull PsiFile psiFile, @NotNull String name, String details) {
    PsiClass psiClass = findClass(psiFile, details);
    if (psiClass != null) {
      return psiClass.findFieldByName(name.substring(0, name.indexOf(" ")), false);
    }
    return null;
  }

  @Nullable
  private static PsiClass findClass(@NotNull PsiFile psiFile, @NotNull String qualifiedName) {
    PsiElement[] children = psiFile.getChildren();
    for (PsiElement element : children) {
      if (element instanceof PsiClass) {
        PsiClass psiClass = (PsiClass) element;
        String className = psiClass.getQualifiedName();
        if (qualifiedName.startsWith(className)) {
          if (qualifiedName.equals(className)) {
            return psiClass;
          }
          String[] path = qualifiedName.substring(className.length() + 1).split("\\.");
          for (String subclass : path) {
            psiClass = psiClass.findInnerClassByName(subclass, false);
            if (psiClass == null) {
              throw new AssertionError("Can't find subclass");
            }
          }
          return psiClass;
        }
      }
    }
    return null;
  }

  private static boolean foldable(@NotNull RefactoringInfo info) {
    return info.getName().contains("Move")
        || info.getName().contains("Pull Up")
        || info.getName().contains("Push Down");
  }

  private static final class Positions {
    final public int hintOffset;
    final public int foldingStartOffset;
    final public int foldingEndOffset;

    private Positions(int hintOffset, int foldingStartOffset, int foldingEndOffset) {
      this.hintOffset = hintOffset;
      this.foldingStartOffset = foldingStartOffset;
      this.foldingEndOffset = foldingEndOffset;
    }
  }

  private enum Element {
    CLASS,
    METHOD,
    FIELD
  }
}

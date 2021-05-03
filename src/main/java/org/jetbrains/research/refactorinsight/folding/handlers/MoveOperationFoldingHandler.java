package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.utils.PsiUtils;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class MoveOperationFoldingHandler implements FoldingHandler {
  @NotNull
  @Override
  public List<Folding> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
    PsiMethod method = PsiUtils.findMethod(file, isBefore ? info.getNameBefore() : info.getNameAfter());
    if (method == null) {
      return Collections.emptyList();
    }
    String details = isBefore ? info.getLeftPath() : info.getRightPath();
    String hintText = specificOperation(info.getType())
        + (isBefore ? " to " : " from ")
        + details.substring(details.lastIndexOf(File.separatorChar) + 1)
        + (info.isChanged() ? ". With changes." : ". Without changes.");
    PsiCodeBlock body = method.getBody();
    return Collections.singletonList(
        new Folding(
            hintText,
            method.getTextRange().getStartOffset(),
            body == null ? -1 : body.getTextRange().getStartOffset(),
            body == null ? -1 : body.getTextRange().getEndOffset()
        )
    );
  }

  @NotNull
  @Override
  public Folding uniteFolds(@NotNull List<Folding> folds) {
    String operation;
    if (folds.stream().allMatch(folding -> folding.hintText.startsWith("Moved"))) {
      operation = "Moved";
    } else if (folds.stream().allMatch(folding -> folding.hintText.startsWith("Pulled up"))) {
      operation = "Pulled up";
    } else if (folds.stream().allMatch(folding -> folding.hintText.startsWith("Pushed down"))) {
      operation = "Pushed down";
    } else {
      throw new AssertionError("Folds of different types");
    }
    String hintText = folds.stream().allMatch(folding -> folding.hintText.contains("without changes"))
        ? operation + " without changes" : operation;
    return new Folding(
        hintText,
        folds.get(0).hintOffset,
        folds.get(0).foldingStartOffset,
        folds.get(0).foldingEndOffset
    );
  }

  private String specificOperation(RefactoringType type) {
    switch (type) {
      case MOVE_OPERATION:
        return "Moved";
      case PULL_UP_OPERATION:
        return "Pulled up";
      case PUSH_DOWN_OPERATION:
        return "Pushed down";
      default:
        throw new AssertionError("Illegal refactoring type");
    }
  }
}

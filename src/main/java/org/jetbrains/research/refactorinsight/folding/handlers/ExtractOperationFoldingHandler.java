package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.utils.PsiUtils;

import java.util.Collections;
import java.util.List;

public class ExtractOperationFoldingHandler implements FoldingHandler {
  @NotNull
  @Override
  public List<Folding> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
    if (isBefore) {
      return Collections.emptyList();
    }
    String nameAfter = info.getDetailsAfter() + '.' + info.getElementBefore();
    PsiMethod method = PsiUtils.findMethod(file, nameAfter);
    if (method == null) {
      return Collections.emptyList();
    }
    String details = info.getNameBefore();
    String hintText = "Extracted from " + details.substring(details.lastIndexOf('.') + 1, details.indexOf('('));
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
    return new Folding(
        "Extracted",
        folds.get(0).hintOffset,
        folds.get(0).foldingStartOffset,
        folds.get(0).foldingEndOffset
    );
  }
}

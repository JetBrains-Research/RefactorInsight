package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.Folding;
import java.util.List;

public interface FoldingHandler {
  /**
   * Get list of refactoring's folds in a file.
   *
   * @param isBefore Whether the file revision is the parent of the info, otherwise the main revision
   */
  @NotNull
  List<Folding> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore);

  /**
   * Summarize folds of same type that placed at the same offset.
   *
   * @param folds Folds of same type at same offset
   * @return Folding that summarizes them all
   */
  @NotNull
  Folding uniteFolds(@NotNull List<Folding> folds);
}

package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;

import java.util.List;

public interface FoldingHandler {
  /**
   * Gets {@link FoldingDescriptor} for the discovered refactoring.
   *
   * @param isBefore Whether the file revision is the parent of the info, otherwise the main revision
   */
  @NotNull
  List<FoldingDescriptor> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore);

  /**
   * Summarizes folds of the same type that placed at the same offset.
   *
   * @param folds Folds of same type at same offset
   * @return Folding that summarizes them all
   */
  @NotNull
  FoldingDescriptor uniteFolds(@NotNull List<FoldingDescriptor> folds);
}

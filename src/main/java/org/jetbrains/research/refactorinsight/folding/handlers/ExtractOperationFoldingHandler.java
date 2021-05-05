package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.Folding;
import java.util.Collections;
import java.util.List;

public class ExtractOperationFoldingHandler implements FoldingHandler {
  @NotNull
  @Override
  public List<Folding> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
    String path = info.getMidPath() != null ? info.getMidPath() : info.getRightPath();
    if (isBefore || !file.getVirtualFile().getPath().endsWith(path)) {
      return Collections.emptyList();
    }
    String details = info.getNameBefore();
    String hintText = "Extracted from " + details.substring(details.lastIndexOf('.') + 1, details.indexOf('('));
    return Collections.singletonList(
        new Folding(
            hintText,
            info.getFoldingPositionsMid()
        )
    );
  }

  @NotNull
  @Override
  public Folding uniteFolds(@NotNull List<Folding> folds) {
    return new Folding(
        "Extracted",
        folds.get(0).positions
    );
  }
}

package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.Folding;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class InlineOperationFoldingHandler implements FoldingHandler {
  @NotNull
  @Override
  public List<Folding> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
    String path = info.getMidPath() != null ? info.getMidPath() : info.getLeftPath();
    if (!isBefore || !file.getVirtualFile().getPath().endsWith(path)) {
      return Collections.emptyList();
    }
    String details = info.getNameAfter();
    String hintText = "Inlined to " + details.substring(details.lastIndexOf('.') + 1, details.indexOf('('));
    if (info.getType() == RefactoringType.MOVE_AND_INLINE_OPERATION) {
      hintText += " in " + info.getRightPath().substring(info.getRightPath().lastIndexOf(File.separatorChar) + 1);
    }
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
        "Inlined",
        folds.get(0).positions
    );
  }
}

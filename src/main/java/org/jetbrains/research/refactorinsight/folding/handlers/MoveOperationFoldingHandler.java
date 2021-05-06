package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.Folding;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class MoveOperationFoldingHandler implements FoldingHandler {
  @NotNull
  @Override
  public List<Folding> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
    if (isBefore && !file.getVirtualFile().getPath().endsWith(info.getLeftPath())) {
      return Collections.emptyList();
    }
    if (!isBefore && !file.getVirtualFile().getPath().endsWith(info.getRightPath())) {
      return Collections.emptyList();
    }
    String details = isBefore ? info.getLeftPath() : info.getRightPath();
    String hintText = specificOperation(info.getType())
        + (isBefore ? " to " : " from ")
        + details.substring(details.lastIndexOf(File.separatorChar) + 1)
        + (info.isChanged() ? " with changes" : " without changes");
    return Collections.singletonList(
        new Folding(
            hintText,
            isBefore ? info.getFoldingPositionsBefore() : info.getFoldingPositionsAfter()
        )
    );
  }

  @NotNull
  @Override
  public Folding uniteFolds(@NotNull List<Folding> folds) {
    String hintText;
    if (folds.stream().allMatch(folding -> folding.hintText.startsWith("Moved with renaming"))) {
      hintText = "Moved with renaming";
    } else if (folds.stream().allMatch(folding -> folding.hintText.startsWith("Moved"))) {
      hintText = "Moved";
    } else if (folds.stream().allMatch(folding -> folding.hintText.startsWith("Pulled up"))) {
      hintText = "Pulled up";
    } else if (folds.stream().allMatch(folding -> folding.hintText.startsWith("Pushed down"))) {
      hintText = "Pushed down";
    } else {
      throw new AssertionError("Folds of different types");
    }
    boolean notChanged = folds.stream().allMatch(folding -> folding.hintText.contains("without changes"));
    if (notChanged) {
      hintText += " without changes";
    }
    return new Folding(
        hintText,
        folds.get(0).positions
    );
  }

  @NotNull
  @Contract(pure = true)
  private String specificOperation(@NotNull RefactoringType type) {
    switch (type) {
      case MOVE_OPERATION:
        return "Moved";
      case MOVE_AND_RENAME_OPERATION:
        return "Moved with renaming";
      case PULL_UP_OPERATION:
        return "Pulled up";
      case PUSH_DOWN_OPERATION:
        return "Pushed down";
      default:
        throw new AssertionError("Illegal refactoring type");
    }
  }
}

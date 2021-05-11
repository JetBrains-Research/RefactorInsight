package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.Folding;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractOperationFoldingHandler implements FoldingHandler {
  @NotNull
  @Override
  public List<Folding> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
    String path = info.getMidPath() != null ? info.getMidPath() : info.getRightPath();
    if (isBefore || !file.getVirtualFile().getPath().endsWith(path)) {
      return Collections.emptyList();
    }
    String details = info.getNameBefore();
    String hintText = "Extracted from "
        + details.substring(details.lastIndexOf('.') + 1, details.indexOf('(') + 1) + ')';
    if (info.getType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      hintText += " in " + info.getLeftPath().substring(info.getLeftPath().lastIndexOf(File.separatorChar) + 1);
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
    List<String> destinations = folds.stream()
        .map(folding -> folding.hintText.substring("Extracted from ".length()))
        .collect(Collectors.toList());
    String hintText = "Extracted from ";
    if (destinations.size() < 4) {
      hintText += String.join(", ", destinations);
    } else {
      hintText += destinations.size() + " methods";
    }
    return new Folding(
        hintText,
        folds.get(0).positions
    );
  }
}

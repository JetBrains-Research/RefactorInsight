package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.research.refactorinsight.adapters.RefactoringType;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;
import org.jetbrains.research.refactorinsight.folding.FoldingDescriptor;
import org.jetbrains.research.refactorinsight.utils.Utils;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractOperationFoldingHandler implements FoldingHandler {
  @NotNull
  @Override
  public List<FoldingDescriptor> getFolds(@NotNull RefactoringInfo info, @NotNull PsiFile file, boolean isBefore) {
    String path = info.getMidPath() != null ? info.getMidPath() : info.getRightPath();
    if (isBefore || !file.getVirtualFile().getPath().endsWith(path)) {
      return Collections.emptyList();
    }
    String hintText = "Extracted from " + Utils.functionSimpleName(info.getNameBefore());
    if (info.getType() == RefactoringType.EXTRACT_AND_MOVE_OPERATION) {
      hintText += " in " + info.getLeftPath().substring(info.getLeftPath().lastIndexOf(File.separatorChar) + 1);
    }
    FoldingDescriptor descriptor = info.getFoldingDescriptorMid();
    descriptor.addHintText(hintText);
    return Collections.singletonList(
            descriptor
    );
  }

  @NotNull
  @Override
  public FoldingDescriptor uniteFolds(@NotNull List<FoldingDescriptor> folds) {
    List<String> destinations = folds.stream()
        .map(folding -> folding.hintText().substring("Extracted from ".length()))
        .distinct()
        .collect(Collectors.toList());
    String hintText = "Extracted from ";
    if (destinations.size() < 4) {
      hintText += String.join(", ", destinations);
    } else {
      hintText += destinations.size() + " methods";
    }
    FoldingDescriptor descriptor = folds.get(0);
    descriptor.addHintText(hintText);
    return descriptor;
  }
}

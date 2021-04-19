package org.jetbrains.research.refactorinsight.folding.handlers;

import com.intellij.psi.PsiFile;
import org.jetbrains.research.refactorinsight.data.RefactoringInfo;

import java.util.List;

public interface FoldingHandler {
  List<Folding> getFolds(RefactoringInfo info, PsiFile file, boolean isBefore);

  final class Folding {
    public final String hintText;
    public final int hintOffset;
    public final int foldingStartOffset;
    public final int foldingEndOffset;

    public Folding(String hintText, int hintOffset, int foldingStartOffset, int foldingEndOffset) {
      this.hintText = hintText;
      this.hintOffset = hintOffset;
      this.foldingStartOffset = foldingStartOffset;
      this.foldingEndOffset = foldingEndOffset;
    }
  }
}

package data.types.classes;

import data.RefactoringEntry;
import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;

public class ExtractClassHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    ExtractClassRefactoring ref = (ExtractClassRefactoring) refactoring;

    return new RefactoringInfo(Type.CLASS)
        .setType(ref.getRefactoringType())
        .setName(ref.getName())
        .setText(ref.toString())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getOriginalClass().codeRange())))
        .setRightSide(Arrays.asList(new TrueCodeRange(ref.getExtractedClass().codeRange())));
  }
}

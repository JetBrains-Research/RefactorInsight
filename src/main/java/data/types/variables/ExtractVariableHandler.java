package data.types.variables;

import data.RefactoringEntry;
import data.RefactoringInfo;
import data.TrueCodeRange;
import data.Type;
import data.types.Handler;
import gr.uom.java.xmi.diff.ExtractVariableRefactoring;
import java.util.Arrays;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

public class ExtractVariableHandler implements Handler {

  @Override
  public RefactoringInfo handle(Refactoring refactoring) {
    ExtractVariableRefactoring ref = (ExtractVariableRefactoring) refactoring;
    return new RefactoringInfo(Type.VARIABLE)
        .setType(RefactoringType.EXTRACT_VARIABLE)
        .setName(ref.getName())
        .setText(ref.toString())
        .setLeftSide(Arrays.asList(new TrueCodeRange(ref.getVariableDeclaration().codeRange())))
        .setRightSide(
            Arrays.asList(new TrueCodeRange(ref.getExtractedVariableDeclarationCodeRange())));
  }
}

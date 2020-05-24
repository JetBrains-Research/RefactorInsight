package data.types.methods;

import data.RefactoringInfo;
import data.types.Handler;
import org.refactoringminer.api.Refactoring;

public class ChangeMethodSignatureHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    //Is not supported by RefactoringMiner yet
    return null;
  }
}

package refactoringInfo;

import java.util.HashMap;
import java.util.Map;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import refactoringInfo.types.Handler;
import refactoringInfo.types.RenameClassHandler;

public class InfoFactory {

  private Map<RefactoringType, Handler> refactoringHandlers = new HashMap<>();

  public InfoFactory() {
    refactoringHandlers.put(RefactoringType.RENAME_CLASS, new RenameClassHandler());
  }

  public RefactoringInfo create(Refactoring refactoring) {
    return refactoringHandlers.get(refactoring.getRefactoringType()).handle(refactoring);
  }
}

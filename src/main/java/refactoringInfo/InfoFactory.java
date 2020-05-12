package refactoringInfo;

import java.util.HashMap;
import java.util.Map;
import org.refactoringminer.api.RefactoringType;
import refactoringInfo.types.Handler;

public class InfoFactory {

  private Map<RefactoringType, Handler> refactoringHandlers = new HashMap<>();

  public InfoFactory() {
    refactoringHandlers.put(RefactoringType.RENAME_CLASS, );
  }

}

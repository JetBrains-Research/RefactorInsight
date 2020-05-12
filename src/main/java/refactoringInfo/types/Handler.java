package refactoringInfo.types;

import org.refactoringminer.api.Refactoring;
import refactoringInfo.RefactoringInfo;

public interface Handler {

  RefactoringInfo handle(Refactoring refactoring);

}

package refactoringInfo.types;

import org.refactoringminer.api.Refactoring;
import refactoringInfo.RefactoringInfo;
import refactoringInfo.TrueCodeRange;

import java.util.List;
import java.util.stream.Collectors;

public interface Handler {

  default RefactoringInfo handle(Refactoring refactoring) {
    List<TrueCodeRange> left = refactoring.leftSide().stream().map(TrueCodeRange::new).collect(Collectors.toList());
    List<TrueCodeRange> right = refactoring.rightSide().stream().map(TrueCodeRange::new).collect(Collectors.toList());
    return new RefactoringInfo(refactoring.getName(), refactoring.toString(), refactoring.getRefactoringType(), left, right);
  }

}

package data.types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import javassist.NotFoundException;

public class RenameClassHandler implements Handler {

  //  @Override
  //  public RefactoringInfo handle(Refactoring refactoring) {
  //    RenameClassRefactoring ref = (RenameClassRefactoring) refactoring;
  //    RefactoringInfo refactoringInfo = new RefactoringInfo(ref);
  //    refactoringInfo.getRenames().put(ref.getOriginalClassName(),
  //        ref.getRenamedClassName());
  //    try {
  //      TrueCodeRange leftCodeRange = refactoringInfo.getLeftSide().get(0);
  //      int[] nameIndeces = getNameIndex(leftCodeRange.getCodeElement(),
  //                              ref.getOriginalClassName());
  //
  //      refactoringInfo.getLeftSide().get(0).incrementTrueStartLine(nameIndeces[0]);
  //    } catch (Exception e) {
  //      e.printStackTrace();
  //    }
  //
  //    return refactoringInfo;
  //  }

  private int[] getNameIndex(String text, String name) throws IOException, NotFoundException {
    BufferedReader reader = new BufferedReader(new StringReader(text));
    int[] startIndeces = {0, -1};
    String line = reader.readLine();
    while (line != null) {
      if (line.contains(name)) {
        startIndeces[1] = line.indexOf(name);
        break;
      } else {
        line = reader.readLine();
        startIndeces[0]++;
      }
    }
    if (startIndeces[1] == -1) {
      throw new NotFoundException(name);
    }
    return startIndeces;
  }

}

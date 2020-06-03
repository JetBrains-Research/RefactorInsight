package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameVariableRefactoring;
import org.refactoringminer.api.Refactoring;

public class RenameVariableHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameVariableRefactoring ref = (RenameVariableRefactoring) refactoring;
    String id = ref.getOperationAfter().getClassName() + ".";
    if ((ref.getOperationAfter().isConstructor() || ref.getOperationAfter().isSetter())
        && ref.getRenamedVariable().isParameter()) {
      id += ref.getRenamedVariable().getVariableName();
    } else {
      id = calculateSignature(ref.getOperationAfter()) + "."
          + ref.getRenamedVariable().getVariableName();
    }
    info.setGroupId(id);
    return info.setGroup(Group.VARIABLE)
        .setElementBefore(ref.getOriginalVariable().toQualifiedString())
        .setElementAfter(ref.getRenamedVariable().toQualifiedString())
        .setNameBefore("in method " + ref.getOperationAfter().getName())
        .setNameAfter("in method " + ref.getOperationAfter().getName())
        .addMarking(ref.getOriginalVariable().codeRange(), ref.getRenamedVariable().codeRange(),
            line -> line.addOffset(ref.getOriginalVariable().getLocationInfo(),
                ref.getRenamedVariable().getLocationInfo()));
  }
}

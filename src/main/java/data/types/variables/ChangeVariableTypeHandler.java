package data.types.variables;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ChangeVariableTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ChangeVariableTypeRefactoring ref = (ChangeVariableTypeRefactoring) refactoring;
    final UMLOperation operationAfter = ref.getOperationAfter();
    String id = operationAfter.getClassName() + ".";
    if ((operationAfter.isSetter() || operationAfter.isConstructor())
        && ref.getChangedTypeVariable().isParameter()) {
      id += ref.getChangedTypeVariable().getVariableName();
    } else {
      id = calculateSignature(ref.getOperationAfter()) + "."
          + ref.getChangedTypeVariable().getVariableName();
    }
    info.setGroupId(id);
    return info.setGroup(Group.VARIABLE)
        .setElementBefore(ref.getOriginalVariable().toQualifiedString())
        .setElementAfter(ref.getChangedTypeVariable().toQualifiedString())
        .setNameBefore("in method " + ref.getOperationAfter().getName())
        .setNameAfter("in method " + ref.getOperationAfter().getName())
        .addMarking(ref.getOriginalVariable().codeRange(),
            ref.getChangedTypeVariable().codeRange(),
            line -> line.addOffset(ref.getOriginalVariable().getType().getLocationInfo(),
                ref.getChangedTypeVariable().getType().getLocationInfo()));
  }
}

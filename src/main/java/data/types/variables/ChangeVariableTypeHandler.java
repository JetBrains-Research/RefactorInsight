package data.types.variables;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.ChangeVariableTypeRefactoring;
import org.refactoringminer.api.Refactoring;

public class ChangeVariableTypeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
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
    if (ref.getChangedTypeVariable().isParameter()) {
      info.setGroup(Group.PARAMETER);
    } else {
      info.setGroup(Group.VARIABLE);
    }
    return info
        .setElementBefore(ref.getOriginalVariable().toQualifiedString())
        .setElementAfter(ref.getChangedTypeVariable().toQualifiedString())
        .setNameBefore("in method " + ref.getOperationAfter().getName())
        .setNameAfter("in method " + ref.getOperationAfter().getName())
        .addMarking(ref.getOriginalVariable().getType().codeRange(),
            ref.getChangedTypeVariable().getType().codeRange());

  }
}

package data.types.methods;

import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.RemoveParameterRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class RemoveParameterHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RemoveParameterRefactoring ref = (RemoveParameterRefactoring) refactoring;

    String classNameBefore = ref.getOperationBefore().getClassName();
    String classNameAfter = ref.getOperationAfter().getClassName();

    return info.setGroup(RefactoringInfo.Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(StringUtils.calculateSignature(ref.getOperationBefore()))
        .setNameAfter(StringUtils.calculateSignature(ref.getOperationAfter()))
        .setElementBefore(ref.getParameter().getVariableDeclaration().toQualifiedString())
        .setElementAfter(null)
        .addMarking(ref.getOperationBefore().codeRange(), ref.getOperationAfter().codeRange(),
            line -> line.addOffset(
                ref.getParameter().getVariableDeclaration().getLocationInfo(),
                RefactoringLine.MarkingOption.REMOVE)
                .setHasColumns(false),
            RefactoringLine.MarkingOption.NONE, true);
  }
}

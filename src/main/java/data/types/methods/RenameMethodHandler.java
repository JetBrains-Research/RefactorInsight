package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import java.util.List;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class RenameMethodHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;

    String id = ref.getRenamedOperation().getClassName() + ".";
    if (ref.getRenamedOperation().isGetter()) {
      List<String> variables = ref.getRenamedOperation().getBody().getAllVariables();
      if (!variables.isEmpty()) {
        id += variables.get(0);
        info.setGroupId(id);
      }
    }
    if (ref.getRenamedOperation().isSetter()) {
      if (!ref.getRenamedOperation().getParameterNameList().isEmpty()) {
        id += ref.getRenamedOperation().getParameterNameList().get(0);
        info.setGroupId(id);
      }
    }

    String classNameBefore = ref.getOriginalOperation().getClassName();
    String classNameAfter = ref.getRenamedOperation().getClassName();

    info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(null)
        .setElementAfter(null)
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getRenamedOperation()))
        .addMarking(ref.getOriginalOperation().getBody().getCompositeStatement().codeRange(),
            ref.getRenamedOperation().getBody().getCompositeStatement().codeRange(),
            refactoringLine -> refactoringLine.setHasColumns(false),
            RefactoringLine.MarkingOption.NONE, false);


    if (ref.getOriginalOperation().getBody() == null) {
      return info.addMarking(ref.getOriginalOperation().codeRange(),
          ref.getRenamedOperation().codeRange(), false);
    }
    return info.addMarking(
        ref.getOriginalOperation().getBody().getCompositeStatement().codeRange(),
        ref.getRenamedOperation().getBody().getCompositeStatement().codeRange(),
        refactoringLine -> {
          refactoringLine.setWord(new String[] {
              ref.getOriginalOperation().getName(),
              null,
              ref.getRenamedOperation().getName()
          });
        },
        RefactoringLine.MarkingOption.COLLAPSE,
        true);
  }
}

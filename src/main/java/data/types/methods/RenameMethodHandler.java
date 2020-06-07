package data.types.methods;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class RenameMethodHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    RenameOperationRefactoring ref = (RenameOperationRefactoring) refactoring;

    String id = ref.getRenamedOperation().getClassName() + ".";
    if (ref.getRenamedOperation().isGetter()) {
      id += ref.getRenamedOperation().getBody().getAllVariables().get(0);
      info.setGroupId(id);
    }
    if (ref.getRenamedOperation().isSetter()) {
      id += ref.getRenamedOperation().getParameterNameList().get(0);
      info.setGroupId(id);
    }

    return info.setGroup(Group.METHOD)
        .setElementBefore(null)
        .setElementAfter(null)
        .addMarking(ref.getOriginalOperation().getBody().getCompositeStatement().codeRange(),
            ref.getRenamedOperation().getBody().getCompositeStatement().codeRange(),
            refactoringLine -> refactoringLine.setHasColumns(false),
            RefactoringLine.MarkingOption.NONE,false)
        .addMarking(
            ref.getOriginalOperation().getBody().getCompositeStatement().codeRange(),
            ref.getRenamedOperation().getBody().getCompositeStatement().codeRange(),
            refactoringLine -> {
              refactoringLine.setLazilyHighlightableWords(new String[]{
                  ref.getOriginalOperation().getName(),
                  null,
                  ref.getRenamedOperation().getName()
              });
            },
            RefactoringLine.MarkingOption.COLLAPSE,
            true)
        .setNameBefore(Utils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(Utils.calculateSignature(ref.getRenamedOperation()));
  }
}

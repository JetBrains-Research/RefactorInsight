package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.SplitAttributeRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class SplitAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    SplitAttributeRefactoring ref = (SplitAttributeRefactoring) refactoring;

    ref.getSplitAttributes().forEach(attr ->
        info.addMarking(ref.getOldAttribute().codeRange(), attr.codeRange()));

    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(ref.getOldAttribute().getVariableName())
        .setElementAfter(ref.getSplitAttributes().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setNameBefore(ref.getOldAttribute().getVariableName())
        .setElementAfter(ref.getOldAttribute().getVariableName());
  }
}

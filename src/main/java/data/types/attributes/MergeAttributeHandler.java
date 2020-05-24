package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MergeAttributeRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class MergeAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MergeAttributeRefactoring ref = (MergeAttributeRefactoring) refactoring;

    ref.getMergedAttributes().forEach(attr ->
        info.addMarking(attr.codeRange(), ref.getNewAttribute().codeRange()));

    return info.setGroup(Group.ATTRIBUTE)
        .setElementBefore(ref.getMergedAttributes().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setElementAfter(ref.getNewAttribute().getVariableName())
        .setNameBefore(ref.getNewAttribute().getVariableName())
        .setNameAfter(ref.getNewAttribute().getVariableName());

  }
}

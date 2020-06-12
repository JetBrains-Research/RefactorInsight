package data.types.attributes;

import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MergeAttributeRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;

public class MergeAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MergeAttributeRefactoring ref = (MergeAttributeRefactoring) refactoring;

    String classNameAfter = ref.getClassNameAfter();
    String classNameBefore = ref.getClassNameBefore();

    ref.getMergedAttributes().forEach(attr ->
        info.addMarking(attr.codeRange(), ref.getNewAttribute().codeRange(), true));

    return info.setGroup(RefactoringInfo.Group.ATTRIBUTE)
        .setNameBefore(classNameBefore)
        .setNameAfter(classNameAfter)
        .setElementBefore(ref.getMergedAttributes().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setElementAfter(ref.getNewAttribute().getVariableDeclaration().getVariableName());

  }
}

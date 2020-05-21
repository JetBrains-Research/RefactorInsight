package data.types.attributes;

import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MergeAttributeRefactoring;
import org.refactoringminer.api.Refactoring;

public class MergeAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    MergeAttributeRefactoring ref = (MergeAttributeRefactoring) refactoring;

    ref.getMergedAttributes().forEach(attr ->
        info.addMarking(attr.codeRange(), ref.getNewAttribute().codeRange()));

    return info.setGroup(Group.ATTRIBUTE);
  }
}

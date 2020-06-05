package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class InlineOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;
    return info.setGroup(Group.METHOD)
        .setElementBefore(ref.getInlinedOperation().getName())
        .setElementAfter(" in method " + ref.getTargetOperationAfterInline().getName())
        .setNameBefore(Utils.calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(Utils.calculateSignature(ref.getTargetOperationAfterInline()))
        .addMarking(ref.getInlinedCodeRangeFromInlinedOperation(),
            ref.getInlinedCodeRangeInTargetOperation());
  }
}

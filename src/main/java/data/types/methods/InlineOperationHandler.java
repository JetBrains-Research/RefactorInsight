package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class InlineOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    InlineOperationRefactoring ref = (InlineOperationRefactoring) refactoring;

    String classNameBefore = ref.getTargetOperationBeforeInline().getClassName();
    String classNameAfter = ref.getTargetOperationAfterInline().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setElementBefore(StringUtils.calculateSignature(ref.getInlinedOperation()))
        .setElementAfter(null)
        .setNameBefore(StringUtils.calculateSignature(ref.getTargetOperationBeforeInline()))
        .setNameAfter(StringUtils.calculateSignature(ref.getTargetOperationAfterInline()))
        .addMarking(ref.getInlinedCodeRangeFromInlinedOperation(),
            ref.getInlinedCodeRangeInTargetOperation());
  }
}

package data.types.methods;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import utils.StringUtils;

public class PullUpOperationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    PullUpOperationRefactoring ref = (PullUpOperationRefactoring) refactoring;
    String classBefore = ref.getOriginalOperation().getClassName();
    String classAfter = ref.getMovedOperation().getClassName();

    return info.setGroup(Group.METHOD)
        .setDetailsBefore(classBefore)
        .setDetailsAfter(classAfter)
        .addMarking(ref.getSourceOperationCodeRangeBeforeMove(),
            ref.getTargetOperationCodeRangeAfterMove())
        .setNameBefore(StringUtils.calculateSignature(ref.getOriginalOperation()))
        .setNameAfter(StringUtils.calculateSignature(ref.getMovedOperation()));
  }
}

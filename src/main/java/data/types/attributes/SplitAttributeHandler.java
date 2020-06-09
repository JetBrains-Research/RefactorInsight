package data.types.attributes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.SplitAttributeRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class SplitAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    SplitAttributeRefactoring ref = (SplitAttributeRefactoring) refactoring;
    ref.getSplitAttributes().forEach(attr ->
        info.addMarking(ref.getOldAttribute().codeRange(), attr.codeRange()));

    String classNameBefore = ref.getClassNameBefore();
    String classNameAfter = ref.getClassNameAfter();

    return info.setGroup(Group.ATTRIBUTE)
        .setNameBefore(classNameBefore)
        .setNameAfter(classNameAfter)
        .setElementBefore(ref.getOldAttribute().getVariableName())
        .setElementAfter(ref.getSplitAttributes().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()));
  }
}

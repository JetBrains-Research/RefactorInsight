package data.types.attributes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.MergeAttributeRefactoring;
import java.util.stream.Collectors;
import org.refactoringminer.api.Refactoring;
import utils.Utils;

public class MergeAttributeHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    MergeAttributeRefactoring ref = (MergeAttributeRefactoring) refactoring;

    String classNameAfter = ref.getClassNameAfter();
    String classNameBefore = ref.getClassNameBefore();

    ref.getMergedAttributes().forEach(attr ->
        info.addMarking(attr.codeRange(), ref.getNewAttribute().codeRange()));

    return info.setGroup(Group.ATTRIBUTE)
        .setDetailsBefore(classNameBefore)
        .setDetailsAfter(classNameAfter)
        .setNameBefore(ref.getMergedAttributes().stream().map(x -> x.getVariableName()).collect(
            Collectors.joining()))
        .setNameAfter(ref.getNewAttribute().getVariableDeclaration().getVariableName());

  }
}

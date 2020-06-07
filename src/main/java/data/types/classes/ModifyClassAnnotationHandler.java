package data.types.classes;

import com.intellij.openapi.project.Project;
import data.Group;
import data.RefactoringInfo;
import data.types.Handler;
import gr.uom.java.xmi.diff.ModifyClassAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class ModifyClassAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info, Project project) {
    ModifyClassAnnotationRefactoring ref = (ModifyClassAnnotationRefactoring) refactoring;
    return info.setGroup(Group.CLASS)
        .setDetailsBefore(ref.getClassBefore().getPackageName())
        .setDetailsAfter(ref.getClassAfter().getPackageName())
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .addMarking(ref.getAnnotationBefore().codeRange(), ref.getAnnotationAfter().codeRange(),
            line -> line.addOffset(ref.getAnnotationBefore().getLocationInfo(),
                ref.getAnnotationAfter().getLocationInfo()));
  }
}
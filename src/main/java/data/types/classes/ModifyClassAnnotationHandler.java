package data.types.classes;

import data.Group;
import data.RefactoringInfo;
import data.RefactoringLine;
import data.types.Handler;
import gr.uom.java.xmi.diff.ModifyClassAnnotationRefactoring;
import org.refactoringminer.api.Refactoring;

public class ModifyClassAnnotationHandler extends Handler {

  @Override
  public RefactoringInfo specify(Refactoring refactoring, RefactoringInfo info) {
    ModifyClassAnnotationRefactoring ref = (ModifyClassAnnotationRefactoring) refactoring;
    return info.setGroup(Group.CLASS)
        .setNameBefore(ref.getClassBefore().getName())
        .setNameAfter(ref.getClassAfter().getName())
        .setElementBefore(ref.getAnnotationBefore().toString())
        .setElementAfter(ref.getAnnotationAfter().toString())
        .addMarking(ref.getAnnotationBefore().codeRange(), ref.getAnnotationAfter().codeRange(),
            line -> line.addOffset(ref.getAnnotationBefore().getLocationInfo(),
                ref.getAnnotationAfter().getLocationInfo()),
            RefactoringLine.MarkingOption.NONE,true);
  }
}